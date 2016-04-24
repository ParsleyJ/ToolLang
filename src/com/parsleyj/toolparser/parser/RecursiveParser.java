package com.parsleyj.toolparser.parser;

import com.parsleyj.toolparser.tokenizer.Token;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.*;
import com.parsleyj.utils.reversiblestream.DoubleSidedReversibleStream;
import com.parsleyj.utils.reversiblestream.ReversibleStream;
import com.parsleyj.utils.reversiblestream.StackedReversibleStream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 17/04/16.
 * TODO: javadoc
 */
public class RecursiveParser implements Parser {

    public static ParseTreeNodeFactory PARSE_TREE_NODE_FACTORY;

    private Grammar grammar;
    private SyntaxClass rootClass;


    public RecursiveParser(Grammar grammar, SyntaxClass rootClass) {
        this.grammar = grammar;
        this.rootClass = rootClass;
    }

    @Override
    public ParseTreeNode parse(List<Token> tokens) {
        PARSE_TREE_NODE_FACTORY = new ParseTreeNodeFactory();
        StackedReversibleStream<ParseTreeNode> ts = new StackedReversibleStream<>(
                promoteHighPriorityTerminals(
                        generateListFromToken(tokens, PARSE_TREE_NODE_FACTORY)));
        //.setDebug(System.err);
        return recursiveDescentParse(ts, null, null, rootClass, null);
    }

    public class InvalidSyntaxCaseDefinitionException extends RuntimeException {
    }

    //todo BUG: if(true)then 1 -> parse failed
    private ParseTreeNode recursiveDescentParse(
            DoubleSidedReversibleStream<ParseTreeNode> ts,
            TokenCategory leftDelimiter,
            TokenCategory rightDelimiter,
            SyntaxClass rootClass,
            SyntaxCase specificCase) {
        if (leftDelimiter != null && rightDelimiter != null)
            throw newParseFailedException(ts.toList());

        ts.checkPoint();
        boolean foundSomething = true;
        boolean enteredWhileFirstTime = false;
        while (foundSomething && !ts.isEmpty() && delimitersCheck(ts, leftDelimiter, rightDelimiter)) {
            enteredWhileFirstTime = true;
            if (oneNodeInStream(ts, leftDelimiter, rightDelimiter)) {
                if ((leftDelimiter == null && rightDelimiter == null && ts.peekLeft().getSyntaxClass().isOrExtends(rootClass)) ||
                        (leftDelimiter != null ? (
                                !ts.peekRight().isTerminal() && ts.peekRight().getSyntaxClass().isOrExtends(rootClass))
                                : (!ts.peekLeft().isTerminal() && ts.peekLeft().getSyntaxClass().isOrExtends(rootClass)))) {
                    try {
                        ParseTreeNode ptn;
                        if (leftDelimiter != null) ptn = ts.popRight();
                        else ptn = ts.popLeft();
                        if (specificCase != null) {
                            if (!ptn.getSyntaxCase().getCaseName().equals(specificCase.getCaseName())) {
                                ts.rollback();
                                throw newParseFailedException(Collections.singletonList(ptn));
                            }
                        }
                        ts.commit();
                        return ptn;
                    } catch (NoEnoughElementsException e) {
                        ts.rollback();
                        throw newParseFailedException(Collections.emptyList());
                    }
                }
            }
            if (specificCase != null) {
                if (specificCase.getParsingDirection() == SyntaxCase.ParsingDirection.LeftToRight) {
                    foundSomething = leftToRightParse(ts, leftDelimiter, false, specificCase);

                } else if (specificCase.getParsingDirection() == SyntaxCase.ParsingDirection.RightToLeft) {
                    foundSomething = rightToLeftParse(ts, rightDelimiter, false, specificCase);

                }
            } else {
                foundSomething = false;
                List<SyntaxCase> candidates = findCandidates(ts);
                for (SyntaxCase candidate : candidates) {

                    if (candidate.getParsingDirection() == SyntaxCase.ParsingDirection.LeftToRight) {
                        foundSomething = leftToRightParse(ts, leftDelimiter, foundSomething, candidate);

                    } else if (candidate.getParsingDirection() == SyntaxCase.ParsingDirection.RightToLeft) {
                        foundSomething = rightToLeftParse(ts, rightDelimiter, foundSomething, candidate);

                    }
                }
            }
        }
        if (foundSomething && enteredWhileFirstTime) {
            return recursiveDescentParse(ts, leftDelimiter, rightDelimiter, rootClass, specificCase);
        }
        else {
            ts.rollback();
            List<ParseTreeNode> l = ts.toList();
            if (l != null) throw newParseFailedException(ts.toList());
            else throw newParseFailedException(new ArrayList<>());
        }
    }

    private boolean delimitersCheck(DoubleSidedReversibleStream<ParseTreeNode> ts, TokenCategory leftDelimiter, TokenCategory rightDelimiter) {
        return (rightDelimiter == null || !ts.peekLeft().isTerminal() || !ts.peekLeft().getTokenCategory().matches(rightDelimiter)) &&
                (leftDelimiter == null || !ts.peekRight().isTerminal() || !ts.peekRight().getTokenCategory().matches(leftDelimiter));
    }

    private boolean rightToLeftParse(DoubleSidedReversibleStream<ParseTreeNode> ts, TokenCategory rightDelimiter, boolean foundSomething, SyntaxCase candidate) {
        List<ParseTreeNode> tmpSequence = new ArrayList<>();
        boolean found = true;
        ts.checkPoint();
        try {
            List<SyntaxCaseComponent> structure = candidate.getStructure();
            if (structure.size() == 1) {
                found = findUniqueComponent(ts, tmpSequence, structure.get(0));
            } else for (int i = 0; i < structure.size(); i++) {
                SyntaxCaseComponent component = structure.get(i);

                SpecificCaseComponent specificCase = null;
                if (component instanceof SpecificCaseComponent) {
                    specificCase = (SpecificCaseComponent) component;
                }

                if (i == 0) {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popLeft();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        SyntaxCaseComponent nextComponent = structure.get(i + 1);
                        if (nextComponent.isTerminal()) {
                            try {
                                DoubleSidedReversibleStream<ParseTreeNode> pns = getLeftSubStream(ts, nextComponent);
                                if (pns == null) {
                                    found = false;
                                    break;
                                }
                                ParseTreeNode node;
                                if (specificCase != null) {
                                    node = recursiveDescentParse(pns, null, null, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                                } else {
                                    node = recursiveDescentParse(pns, null, null, (SyntaxClass) component, null);
                                }
                                tmpSequence.add(node);
                            } catch (ParseFailedException pfe) {
                                found = false;
                                break;
                            }
                        } else {
                            throw new InvalidSyntaxCaseDefinitionException();
                        }
                    }
                } else if (i == structure.size() - 1) {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popLeft();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        try {
                            ParseTreeNode node;
                            if (specificCase != null) {
                                node = recursiveDescentParse(ts, null, rightDelimiter, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                            } else {
                                node = recursiveDescentParse(ts, null, rightDelimiter, (SyntaxClass) component, null);
                            }
                            tmpSequence.add(node);
                        } catch (ParseFailedException pfe) {
                            found = false;
                            break;
                        }
                    }
                } else {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popLeft();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        SyntaxCaseComponent nextComponent = structure.get(i + 1);
                        if (nextComponent.isTerminal()) {
                            try {
                                ParseTreeNode node;
                                if (specificCase != null) {
                                    node = recursiveDescentParse(ts, null, (TokenCategory) nextComponent, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                                } else {
                                    node = recursiveDescentParse(ts, null, (TokenCategory) nextComponent, (SyntaxClass) component, null);
                                }
                                tmpSequence.add(node);
                            } catch (ParseFailedException pfe) {
                                found = false;
                                break;
                            }
                        } else {
                            throw new InvalidSyntaxCaseDefinitionException();
                        }
                    }
                }
            }
        } catch (NoEnoughElementsException e) {
            found = false;
        }
        if (found) {
            foundSomething = true;
            ts.commit();
            ParseTreeNode ptn = newNonTerminalNode(candidate.getBelongingClass(), candidate, tmpSequence);
            ts.pushLeft(ptn);
        } else {
            ts.rollback();
        }
        return foundSomething;
    }

    private boolean leftToRightParse(DoubleSidedReversibleStream<ParseTreeNode> ts, TokenCategory leftDelimiter, boolean foundSomething, SyntaxCase candidate) {
        List<ParseTreeNode> tmpSequence = new ArrayList<>();
        boolean found = true;
        ts.checkPoint();
        try {
            List<SyntaxCaseComponent> structure = candidate.getStructure();
            if (structure.size() == 1)
                found = findUniqueComponent(ts, tmpSequence, structure.get(0));//TODO: check direction ambivalence
            else for (int i = structure.size() - 1; i >= 0; --i) {
                SyntaxCaseComponent component = structure.get(i);

                SpecificCaseComponent specificCase = null;
                if (component instanceof SpecificCaseComponent) {
                    specificCase = (SpecificCaseComponent) component;
                }

                if (i == structure.size() - 1) {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popRight();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(0, terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        SyntaxCaseComponent nextComponent = structure.get(i - 1);
                        if (nextComponent.isTerminal()) {
                            try {
                                DoubleSidedReversibleStream<ParseTreeNode> pns = getRightSubStream(ts, nextComponent);
                                if (pns == null) {
                                    found = false;
                                    break;
                                }
                                ParseTreeNode node;
                                if(specificCase!=null){
                                    node = recursiveDescentParse(pns, null, null, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                                }else{
                                    node = recursiveDescentParse(pns, null, null, (SyntaxClass) component, null);
                                }
                                tmpSequence.add(0, node);
                            } catch (ParseFailedException pfe) {
                                found = false;
                                break;
                            }
                        } else {
                            throw new InvalidSyntaxCaseDefinitionException();
                        }
                    }
                } else if (i == 0) {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popRight();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(0, terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        try {
                            ParseTreeNode node;
                            if (specificCase != null) {
                                node = recursiveDescentParse(ts, leftDelimiter, null, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                            } else {
                                node = recursiveDescentParse(ts, leftDelimiter, null, (SyntaxClass) component, null);
                            }
                            tmpSequence.add(0, node);
                        } catch (ParseFailedException pfe) {
                            found = false;
                            break;
                        }
                    }
                } else {
                    if (component.isTerminal()) {
                        ParseTreeNode terminalNode = ts.popRight();
                        if (terminalNode.isTerminal() && terminalNode.getTokenCategory().matches(component)) {
                            tmpSequence.add(0, terminalNode);
                        } else {
                            found = false;
                            break;
                        }
                    } else {
                        SyntaxCaseComponent nextComponent = structure.get(i - 1);
                        if (nextComponent.isTerminal()) {
                            try {
                                ParseTreeNode node;
                                if (specificCase != null) {
                                    node = recursiveDescentParse(ts, (TokenCategory) nextComponent, null, specificCase.getSyntaxClass(), specificCase.getSyntaxCase());
                                } else {
                                    node = recursiveDescentParse(ts, (TokenCategory) nextComponent, null, (SyntaxClass) component, null);
                                }
                                tmpSequence.add(0, node);
                            } catch (ParseFailedException pfe) {
                                found = false;
                                break;
                            }
                        } else {
                            throw new InvalidSyntaxCaseDefinitionException();
                        }
                    }
                }
            }
        } catch (NoEnoughElementsException e) {
            found = false;
        }
        if (found) {
            foundSomething = true;
            ts.commit();
            ParseTreeNode ptn = newNonTerminalNode(candidate.getBelongingClass(), candidate, tmpSequence);
            ts.pushRight(ptn);
        } else {
            ts.rollback();
        }
        return foundSomething;
    }


    private List<ParseTreeNode> generateListFromToken(List<Token> tokens, ParseTreeNodeFactory stf) {
        List<ParseTreeNode> treeList = new ArrayList<>();
        for (Token t : tokens) {
            TokenCategory tokenCategory = grammar.getTokenCategory(t);
            if (tokenCategory == null) throw new InvalidTokenFoundException(t);
            ParseTreeNode ast = stf.newNodeTree();
            ast.setParsedToken(t);
            ast.setTokenCategory(tokenCategory);
            ast.setTerminal(true);
            treeList.add(ast);
        }
        return treeList;
    }

    private List<SyntaxCase> candidatesStartingWith(List<SyntaxCaseComponent> leftComponents, List<SyntaxCaseComponent> rightComponents) {
        return grammar.getPriorityCaseList().stream()
                .map(Pair::getSecond)
                .filter(syntaxCase -> {//todo: maybe bug: if assignment is r-t-l, in .x = 2 assignment does not get chosen as candidate
                    if (syntaxCase.getParsingDirection() == SyntaxCase.ParsingDirection.RightToLeft) {
                        for (int i = 0; i < leftComponents.size(); ++i) {
                            SyntaxCaseComponent component = leftComponents.get(i);
                            //noinspection Duplicates
                            if(component instanceof SyntaxClass && syntaxCase.getStructure().get(i) instanceof SyntaxClass){
                                if(!((SyntaxClass) component).isOrExtends((SyntaxClass) syntaxCase.getStructure().get(i)))
                                    return false;
                            } else if (!syntaxCase.getStructure().get(i).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                                return false;
                            }
                        }
                        return true;

                    } else if (syntaxCase.getParsingDirection() == SyntaxCase.ParsingDirection.LeftToRight) {
                        for (int i = 0; i < rightComponents.size(); ++i) {
                            SyntaxCaseComponent component = rightComponents.get(i);
                            int j =syntaxCase.getStructure().size() - 1 - i;
                            //noinspection Duplicates
                            if(component instanceof SyntaxClass && syntaxCase.getStructure().get(j) instanceof SyntaxClass){
                                if(!((SyntaxClass) component).isOrExtends((SyntaxClass) syntaxCase.getStructure().get(j)))
                                    return false;
                            } else if (!syntaxCase.getStructure().get(j).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                                return false;
                            }
                        }
                        return true;
                    } else return false;
                }).collect(Collectors.toList());
    }


    private boolean isOnlyTerminals(SyntaxCase sc) {
        for (SyntaxCaseComponent scc : sc.getStructure())
            if (!scc.isTerminal()) return false;
        return true;
    }


    private ParseTreeNode newTerminalNode(Token t) {
        ParseTreeNode ptn = PARSE_TREE_NODE_FACTORY.newNodeTree();
        ptn.setParsedToken(t);
        ptn.setTokenCategory(grammar.getTokenCategory(t));
        ptn.setTerminal(true);
        return ptn;
    }


    private ParseTreeNode newNonTerminalNode(SyntaxClass cl, SyntaxCase cs, List<ParseTreeNode> children) {
        ParseTreeNode ptn = PARSE_TREE_NODE_FACTORY.newNodeTree(children.toArray(new ParseTreeNode[children.size()]));
        ptn.setTerminal(false);
        ptn.setSyntaxCase(cs);
        ptn.setSyntaxClass(cl);
        return ptn;
    }

    private List<ParseTreeNode> promoteHighPriorityTerminals(List<ParseTreeNode> nodes) {
        List<ParseTreeNode> result = nodes;
        List<SyntaxCase> syntaxCases = new ArrayList<>();
        for (SyntaxCase sc : grammar.getPriorityCaseList().stream().map(Pair::getSecond).collect(Collectors.toList())) {
            if (!isOnlyTerminals(sc)) break;
            syntaxCases.add(sc);
        }
        for (SyntaxCase s : syntaxCases) {
            List<ParseTreeNode> tmp = new ArrayList<>();
            int window = s.getStructure().size();
            int start = 0;
            while (start <= result.size() - window) {
                int end = start + window;
                List<ParseTreeNode> currentSubList = result.subList(start, end);
                SyntaxCase instanceCase = new SyntaxCase("", null, currentSubList.stream()
                        .map(a -> a.isTerminal() ? a.getTokenCategory() : new SyntaxParsingInstance(a.getSyntaxClass(), a.getSyntaxCase()))
                        .collect(Collectors.toList())
                        .toArray(new SyntaxCaseComponent[currentSubList.size()]));
                boolean caseMatch = Grammar.caseMatch(instanceCase, s);
                if (caseMatch) {
                    tmp.add(newNonTerminalNode(s.getBelongingClass(), s, currentSubList));
                    start += window - 1;
                } else tmp.add(currentSubList.get(0));
                ++start;
            }
            for (int i = start; i < result.size(); ++i)
                tmp.add(result.get(i));

            result = tmp;
        }
        return result;


    }

    private DoubleSidedReversibleStream<ParseTreeNode> getLeftSubStream(DoubleSidedReversibleStream<ParseTreeNode> ts, SyntaxCaseComponent nextComponent) {
        return (DoubleSidedReversibleStream<ParseTreeNode>) ts.leftSubStreamUntilPredicate((arg1) -> arg1.isTerminal() ?
                arg1.getTokenCategory().matches(nextComponent) :
                arg1.getSyntaxClass().matches(nextComponent));
    }

    private DoubleSidedReversibleStream<ParseTreeNode> getRightSubStream(DoubleSidedReversibleStream<ParseTreeNode> ts, SyntaxCaseComponent nextComponent) {
        return (DoubleSidedReversibleStream<ParseTreeNode>) ts.rightSubStreamUntilPredicate(
                (arg1) -> arg1.isTerminal() ?
                        arg1.getTokenCategory().matches(nextComponent) :
                        arg1.getSyntaxClass().matches(nextComponent));
    }

    private boolean oneNodeInStream(DoubleSidedReversibleStream<ParseTreeNode> ts, TokenCategory leftDelimiter, TokenCategory rightDelimiter) {
        return ts.remainingCount() == 1 || (
                rightDelimiter != null &&
                        ts.remainingCount() > 1 &&
                        ts.peekLeft(1).isTerminal() &&
                        ts.peekLeft(1).getTokenCategory().matches(rightDelimiter)
        ) || (
                leftDelimiter != null &&
                        ts.remainingCount() > 1 &&
                        ts.peekRight(1).isTerminal() &&
                        ts.peekRight(1).getTokenCategory().matches(leftDelimiter)
        );
    }

    private List<SyntaxCase> findCandidates(DoubleSidedReversibleStream<ParseTreeNode> ts) {
        ParseTreeNode firstLeft = ts.peekLeft();
        ParseTreeNode firstRight = ts.peekRight();
        List<SyntaxCase> candidates = PJ.reverse(candidatesStartingWith(
                firstLeft.isTerminal() ? PJ.list(firstLeft.getTokenCategory()) : PJ.list(firstLeft.getSyntaxClass()),
                firstRight.isTerminal() ? PJ.list(firstRight.getTokenCategory()) : PJ.list(firstRight.getSyntaxClass())));
        return candidates.stream().filter(c -> streamContainsTerminals(c.getTerminalSymbols(), ts.toList())).collect(Collectors.toList());
    }


    private boolean streamContainsTerminals(List<? extends SyntaxCaseComponent> terminals, List<ParseTreeNode> sequence){
        //todo (OOO) other improvements can be done:
        //todo  - by checking the exact number of instances of the terminal
        //todo  - by checking the exact order in which the terminals appear
        for(SyntaxCaseComponent scc:terminals){
            if(!scc.isTerminal()) throw new RuntimeException("streamContainsTerminals accepts a list of terminals only as first parameter");
            boolean isPresent = false;
            for(ParseTreeNode ptn: sequence)
                if(ptn.isTerminal() && ptn.getTokenCategory().matches(scc)){
                    isPresent = true;
                    break;
                }
            if(!isPresent) return false;
        }
        return true;
    }

    private boolean findUniqueComponent(ReversibleStream<ParseTreeNode> ts, List<ParseTreeNode> tmpSequence, SyntaxCaseComponent component) throws NoEnoughElementsException {
        if (component.isTerminal()) {
            ParseTreeNode terminalNode = ts.popLeft();
            if (terminalNode.isTerminal() && !terminalNode.getTokenCategory().matches(component)) {
                return false;
            } else {
                tmpSequence.add(terminalNode);
                return true;
            }
        } else {
            throw new InvalidSyntaxCaseDefinitionException();
        }
    }

    private ParseFailedException newParseFailedException(List<ParseTreeNode> foundSequence) {
        ParseTreeNode errorNode = PARSE_TREE_NODE_FACTORY.newNodeTree(foundSequence.toArray(new ParseTreeNode[foundSequence.size()]));
        errorNode.setSyntaxCase(new SyntaxCase("PARSE FAILED", rootClass));
        errorNode.setSyntaxClass(rootClass);
        return new ParseFailedException(errorNode);
    }
}
