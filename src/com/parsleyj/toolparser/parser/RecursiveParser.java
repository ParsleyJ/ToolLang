package com.parsleyj.toolparser.parser;

import com.parsleyj.toolparser.tokenizer.Token;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.*;
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

    private List<SyntaxCase> getAllSyntaxCasesForClass(SyntaxClass clas) {
        return grammar.getPriorityCaseList().stream()
                .map(Pair::getSecond)
                .filter(sc -> sc.getBelongingClass().isOrExtends(clas))
                .collect(Collectors.toList());
    }

    private List<SyntaxCase> candidatesStartingWith(SyntaxCaseComponent... components) {
        return grammar.getPriorityCaseList().stream()
                .map(Pair::getSecond)
                .filter(syntaxCase -> {
                    for (int i = 0; i < components.length; i++) {
                        SyntaxCaseComponent component = components[i];
                        if (!syntaxCase.getStructure().get(i).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    private List<SyntaxCase> candidatesByClassStartingWith(SyntaxClass syntaxClass, SyntaxCaseComponent... components) {
        List<SyntaxCase> list = getAllSyntaxCasesForClass(syntaxClass);
        return list.stream().filter(syntaxCase -> {
            for (int i = 0; i < components.length; i++) {
                SyntaxCaseComponent component = components[i];
                if (!syntaxCase.getStructure().get(i).getSyntaxComponentName().equals(component.getSyntaxComponentName())) {
                    return false;
                }
            }
            return true;
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


    private ParseTreeNode recursiveDescentParse(ReversibleStream<ParseTreeNode> ts, TokenCategory delimiter, SyntaxClass rootClass) {
        ts.checkPoint();
        boolean foundSomething = true;
        while (foundSomething && !ts.isEmpty() &&
                        (delimiter == null ||
                        !ts.peek().isTerminal() ||
                        !ts.peek().getTokenCategory().getTokenClassName().equals(delimiter.getTokenClassName()))) {

            if (ts.remainingCount() == 1 || (
                    delimiter != null &&
                            ts.remainingCount() > 1 &&
                            ts.peek(1).isTerminal() &&
                            ts.peek(1).getTokenCategory().getTokenClassName().equals(delimiter.getTokenClassName()))) {
                if (!ts.peek().isTerminal() && ts.peek().getSyntaxClass().isOrExtends(rootClass)) {
                    try {
                        ParseTreeNode ptn = ts.getNext();
                        ts.commit();
                        return ptn;
                    } catch (NoEnoughElementsException e) {
                        ts.rollback();
                        throw newParseFailedException(Collections.emptyList());
                    }
                }
            }
            foundSomething = false;
            ParseTreeNode first = ts.peek();
            List<SyntaxCase> candidates = PJ.reverse(candidatesStartingWith(first.isTerminal() ? first.getTokenCategory() : first.getSyntaxClass()));
            for (SyntaxCase candidate : candidates) {
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
                                ParseTreeNode terminalNode = ts.getNext();
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
                                        ReversibleStream<ParseTreeNode> pns = ts.subStreamUntilPredicate((arg1) -> arg1.isTerminal() ?
                                                arg1.getTokenCategory().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()) :
                                                arg1.getSyntaxClass().getSyntaxComponentName().equals(nextComponent.getSyntaxComponentName()));
                                        if (pns == null) {
                                            found = false;
                                            break;
                                        }
                                        ParseTreeNode node = recursiveDescentParse(pns, null, (SyntaxClass) component);
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
                                ParseTreeNode terminalNode = ts.getNext();
                                if (!terminalNode.getTokenCategory().getTokenClassName().equals(component.getSyntaxComponentName())) {
                                    found = false;
                                    break;
                                } else {
                                    tmpSequence.add(terminalNode);
                                }
                            } else {
                                try {
                                    ParseTreeNode node = recursiveDescentParse(ts, delimiter, (SyntaxClass) component);
                                    tmpSequence.add(node);
                                } catch (ParseFailedException pfe) {
                                    found = false;
                                    break;
                                }
                            }
                        } else {
                            if (component.isTerminal()) {
                                ParseTreeNode terminalNode = ts.getNext();
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
                                        ParseTreeNode node = recursiveDescentParse(ts, (TokenCategory) nextComponent, (SyntaxClass) component);
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
                    //foundSequence.add(ptn);
                    ts.pushFront(ptn);
                } else {
                    ts.rollback();
                }
            }

        }
        if(foundSomething) return recursiveDescentParse(ts, delimiter, rootClass);
        else throw newParseFailedException(ts.toList());
    }


    private boolean findUniqueComponent(ReversibleStream<ParseTreeNode> ts, List<ParseTreeNode> tmpSequence, SyntaxCaseComponent component) throws NoEnoughElementsException {
        if (component.isTerminal()) {
            ParseTreeNode terminalNode = ts.getNext();
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
        StackedReversibleStream<ParseTreeNode> ts = new StackedReversibleStream<>(promoteHighPriorityTerminals(generateListFromToken(tokens, PARSE_TREE_NODE_FACTORY)));
        return recursiveDescentParse(ts, null, rootClass);
    }

    public class InvalidSyntaxCaseDefinitionException extends RuntimeException {
    }
}
