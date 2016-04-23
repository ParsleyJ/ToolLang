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
                .filter(syntaxCase -> {
                    if (syntaxCase.getParsingDirection() == SyntaxCase.ParsingDirection.RightToLeft) {
                        for (int i = 0; i < leftComponents.size(); ++i) {
                            SyntaxCaseComponent component = leftComponents.get(i);
                            if (!syntaxCase.getStructure().get(i).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                                return false;
                            }
                        }
                        return true;

                    } else if (syntaxCase.getParsingDirection() == SyntaxCase.ParsingDirection.LeftToRight) {
                        for (int i = 0; i < rightComponents.size(); ++i) {
                            SyntaxCaseComponent component = rightComponents.get(i);
                            if (!syntaxCase.getStructure().get(syntaxCase.getStructure().size()-1-i).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                                return false;
                            }
                        }
                        return true;
                    } else return false;
                }).collect(Collectors.toList());
    }


    private boolean areOnlyTerminals(List<ParseTreeNode> ptns) {
        for (ParseTreeNode ptn : ptns)
            if (!ptn.isTerminal()) return false;
        return true;
    }

    private boolean isOnlyTerminals(SyntaxCase sc) {
        for (SyntaxCaseComponent scc : sc.getStructure())
            if (!scc.isTerminal()) return false;
        return true;
    }

    private boolean startsWithTerminal(SyntaxCase sc) {
        return sc.getStructure().get(0).isTerminal();
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
                result.add(result.get(i));

            result = tmp;
        }
        return result;


    }



    private ParseTreeNode recursiveDescentParse(DoubleSidedReversibleStream<ParseTreeNode> ts, TokenCategory leftDelimiter, TokenCategory rightDelimiter, SyntaxClass rootClass) {
        SyntaxCase.ParsingDirection previousParsingDirection;
        if (leftDelimiter != null && rightDelimiter != null)
            throw newParseFailedException(ts.toList());
        else if (leftDelimiter != null)
            previousParsingDirection = SyntaxCase.ParsingDirection.LeftToRight;
        else if (rightDelimiter != null)
            previousParsingDirection = SyntaxCase.ParsingDirection.RightToLeft;
        else
            previousParsingDirection = null;

        ts.checkPoint();
        boolean foundSomething = true;
        while (foundSomething && !ts.isEmpty() && (
                rightDelimiter == null ||
                        !ts.peekLeft().isTerminal() ||
                        !ts.peekLeft().getTokenCategory().getTokenClassName().equals(rightDelimiter.getTokenClassName())
        ) && (
                leftDelimiter == null ||
                        !ts.peekRight().isTerminal() ||
                        !ts.peekRight().getTokenCategory().getTokenClassName().equals(leftDelimiter.getTokenClassName())
        )) {

            if (ts.remainingCount() == 1 || (
                    rightDelimiter != null &&
                            ts.remainingCount() > 1 &&
                            ts.peekLeft(1).isTerminal() &&
                            ts.peekLeft(1).getTokenCategory().getTokenClassName().equals(rightDelimiter.getTokenClassName())
            ) || (
                    leftDelimiter != null &&
                            ts.remainingCount() > 1 &&
                            ts.peekRight(1).isTerminal() &&
                            ts.peekRight(1).getTokenCategory().getTokenClassName().equals(leftDelimiter.getTokenClassName())
            )) {
                if (previousParsingDirection == null ||
                        (previousParsingDirection == SyntaxCase.ParsingDirection.LeftToRight ? (
                        !ts.peekRight().isTerminal() && ts.peekRight().getSyntaxClass().isOrExtends(rootClass))
                        : (!ts.peekLeft().isTerminal() && ts.peekLeft().getSyntaxClass().isOrExtends(rootClass)))) {
                    try {
                        ParseTreeNode ptn;
                        if (leftDelimiter != null) ptn = ts.popRight();
                        else ptn = ts.popLeft();
                        ts.commit();
                        return ptn;
                    } catch (NoEnoughElementsException e) {
                        ts.rollback();
                        throw newParseFailedException(Collections.emptyList());
                    }
                }
            }
            foundSomething = false;
            ParseTreeNode firstLeft = ts.peekLeft();
            ParseTreeNode firstRight = ts.peekRight();
            List<SyntaxCase> candidates = PJ.reverse(candidatesStartingWith(
                    firstLeft.isTerminal() ? PJ.list(firstLeft.getTokenCategory()) : PJ.list(firstLeft.getSyntaxClass()),
                    firstRight.isTerminal() ? PJ.list(firstRight.getTokenCategory()) : PJ.list(firstRight.getSyntaxClass())));
            for (SyntaxCase candidate : candidates) {
                SyntaxCase.ParsingDirection currentParsingDirection = candidate.getParsingDirection();
                if (currentParsingDirection == SyntaxCase.ParsingDirection.LeftToRight) {
                    List<ParseTreeNode> tmpSequence = new ArrayList<>();
                    boolean found = true;
                    ts.checkPoint();
                    try {
                        List<SyntaxCaseComponent> structure = candidate.getStructure();
                        for (int i = structure.size() - 1; i >= 0; --i) {
                            SyntaxCaseComponent component = structure.get(i);

                            if (structure.size() == 1) {
                                found = findUniqueComponent(ts, tmpSequence, component);//TODO: check direction ambivalence
                                break;
                            }

                            if (i == structure.size() - 1) {
                                if (component.isTerminal()) {
                                    ParseTreeNode terminalNode = ts.popRight();
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(0, terminalNode);
                                    }
                                } else {
                                    SyntaxCaseComponent nextComponent = structure.get(i - 1);
                                    if (nextComponent.isTerminal()) {
                                        try {
                                            DoubleSidedReversibleStream<ParseTreeNode> pns =
                                                    (DoubleSidedReversibleStream<ParseTreeNode>) ts.rightSubStreamUntilPredicate((arg1) -> arg1.isTerminal() ?
                                                            arg1.getTokenCategory().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()) :
                                                            arg1.getSyntaxClass().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()));
                                            if (pns == null) {
                                                found = false;
                                                break;
                                            }
                                            ParseTreeNode node = recursiveDescentParse(pns, null, null, (SyntaxClass) component);
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
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(0, terminalNode);
                                    }
                                } else {
                                    try {
                                        ParseTreeNode node = recursiveDescentParse(ts, leftDelimiter, null, (SyntaxClass) component);
                                        tmpSequence.add(0, node);
                                    } catch (ParseFailedException pfe) {
                                        found = false;
                                        break;
                                    }
                                }
                            } else {
                                if (component.isTerminal()) {
                                    ParseTreeNode terminalNode = ts.popRight();
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(0, terminalNode);
                                    }
                                } else {
                                    SyntaxCaseComponent nextComponent = structure.get(i - 1);
                                    if (nextComponent.isTerminal()) {
                                        try {
                                            ParseTreeNode node = recursiveDescentParse(ts, (TokenCategory) nextComponent, null, (SyntaxClass) component);
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
                } else if (currentParsingDirection == SyntaxCase.ParsingDirection.RightToLeft) {
                    List<ParseTreeNode> tmpSequence = new ArrayList<>();
                    boolean found = true;
                    ts.checkPoint();
                    try {
                        List<SyntaxCaseComponent> structure = candidate.getStructure();
                        for (int i = 0; i < structure.size(); i++) {
                            SyntaxCaseComponent component = structure.get(i);

                            if (structure.size() == 1) {
                                found = findUniqueComponent(ts, tmpSequence, component);
                                break;
                            }

                            if (i == 0) {
                                if (component.isTerminal()) {
                                    ParseTreeNode terminalNode = ts.popLeft();
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(terminalNode);
                                    }
                                } else {
                                    SyntaxCaseComponent nextComponent = structure.get(i + 1);
                                    if (nextComponent.isTerminal()) {
                                        try {
                                            DoubleSidedReversibleStream<ParseTreeNode> pns =
                                                    (DoubleSidedReversibleStream<ParseTreeNode>) ts.leftSubStreamUntilPredicate((arg1) -> arg1.isTerminal() ?
                                                            arg1.getTokenCategory().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()) :
                                                            arg1.getSyntaxClass().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()));
                                            if (pns == null) {
                                                found = false;
                                                break;
                                            }
                                            ParseTreeNode node = recursiveDescentParse(pns, null, null, (SyntaxClass) component);
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
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(terminalNode);
                                    }
                                } else {
                                    try {
                                        ParseTreeNode node = recursiveDescentParse(ts, null, rightDelimiter, (SyntaxClass) component);
                                        tmpSequence.add(node);
                                    } catch (ParseFailedException pfe) {
                                        found = false;
                                        break;
                                    }
                                }
                            } else {
                                if (component.isTerminal()) {
                                    ParseTreeNode terminalNode = ts.popLeft();
                                    if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                        found = false;
                                        break;
                                    } else {
                                        tmpSequence.add(terminalNode);
                                    }
                                } else {
                                    SyntaxCaseComponent nextComponent = structure.get(i + 1);
                                    if (nextComponent.isTerminal()) {
                                        try {
                                            ParseTreeNode node = recursiveDescentParse(ts, null, (TokenCategory) nextComponent, (SyntaxClass) component);
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
                }
            }
        }
        if (foundSomething) return recursiveDescentParse(ts, leftDelimiter, rightDelimiter, rootClass);
        else {
            List<ParseTreeNode> l = ts.toList();
            if(l != null) throw newParseFailedException(ts.toList());
            else throw newParseFailedException(new ArrayList<>());
        }
    }


    private boolean findUniqueComponent(ReversibleStream<ParseTreeNode> ts, List<ParseTreeNode> tmpSequence, SyntaxCaseComponent component) throws NoEnoughElementsException {
        if (component.isTerminal()) {
            ParseTreeNode terminalNode = ts.popLeft();
            if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
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


    @Override
    public ParseTreeNode parse(List<Token> tokens) {
        PARSE_TREE_NODE_FACTORY = new ParseTreeNodeFactory();
        StackedReversibleStream<ParseTreeNode> ts = new StackedReversibleStream<>(
                promoteHighPriorityTerminals(
                        generateListFromToken(tokens, PARSE_TREE_NODE_FACTORY)));
                //.setDebug(System.err);
        return recursiveDescentParse(ts, null, null, rootClass);
    }

    public class InvalidSyntaxCaseDefinitionException extends RuntimeException {
    }
}
