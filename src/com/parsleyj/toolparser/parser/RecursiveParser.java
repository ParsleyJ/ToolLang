package com.parsleyj.toolparser.parser;

import com.parsleyj.toolparser.tokenizer.Token;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.PJ;
import com.parsleyj.utils.Pair;

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

    public static class ParseNodeStream {

        private List<ParseTreeNode> nodes;
        private ArrayDeque<Integer> checkPoints = new ArrayDeque<>();
        private int index = 0;

        public ParseNodeStream(List<ParseTreeNode> nodes) {
            this.nodes = nodes;
        }

        public void checkPoint() {
            checkPoints.addFirst(index);
        }

        public void rollback() {
            index = checkPoints.pop();
        }

        public void commit() {
            checkPoints.pop();
        }

        public ParseTreeNode getNext() throws NoEnoughElementsException {
            if(index >= nodes.size()) throw new NoEnoughElementsException();
            return nodes.get(index++);
        }

        public ParseTreeNode get(int index) {
            return nodes.get(index);
        }

        public List<ParseTreeNode> getNext(int howMany) throws NoEnoughElementsException {
            if(index + howMany >= nodes.size()) throw new NoEnoughElementsException();
            List<ParseTreeNode> result = nodes.subList(index, index + howMany);
            index += howMany;
            return result;
        }

        public void pushFront(ParseTreeNode pts){
            nodes.add(0, pts);
        }

        public int moveCursor(int offset) {
            return index += offset;
        }

        public ParseTreeNode peek() {
            return nodes.get(index);
        }

        public ParseTreeNode peek(int index) {
            return nodes.get(index);
        }

        public int getIndex() {
            return index;
        }

        public boolean isEmpty() {
            return index >= nodes.size();
        }

        public int remainingCount() {
            return nodes.size() - index;
        }

        public ParseNodeStream subStreamUntilDelimiter(SyntaxCaseComponent delimiter) {
            if(delimiter == null){
                ParseNodeStream pns = new ParseNodeStream(nodes.subList(index, nodes.size()));
                index = nodes.size();
                return pns;
            }
            for (int i = index; i < nodes.size(); ++i) {
                if (nodes.get(i).isTerminal() ?
                        nodes.get(i).getTokenCategory().getSyntaxComponentName().equals(delimiter.getSyntaxComponentName()) :
                        nodes.get(i).getSyntaxClass().getSyntaxComponentName().equals(delimiter.getSyntaxComponentName())) {
                    ParseNodeStream pns = new ParseNodeStream(nodes.subList(index, i));
                    index = i;
                    return pns;
                }
            }
            return null;
        }

        public ParseNodeStream subStreamUntilDelimiterIncluded(SyntaxCaseComponent delimiter) {
            if(delimiter == null){
                ParseNodeStream pns = new ParseNodeStream(nodes.subList(index, nodes.size()));
                index = nodes.size();
                return pns;
            }
            for (int i = index; i < nodes.size(); ++i) {
                if (nodes.get(i).isTerminal() ?
                        nodes.get(i).getTokenCategory().getSyntaxComponentName().equals(delimiter.getSyntaxComponentName()) :
                        nodes.get(i).getSyntaxClass().getSyntaxComponentName().equals(delimiter.getSyntaxComponentName())) {
                    ParseNodeStream pns = new ParseNodeStream(nodes.subList(index, i+1));
                    index = i+1;
                    return pns;
                }
            }
            return null;
        }

        public List<ParseTreeNode> toList() {
            return nodes.subList(index, nodes.size());
        }

        public class NoEnoughElementsException extends Exception {
        }
    }

    private enum Delta {
        Only_T,
        Mixed
    }

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


    private ParseTreeNode recursiveDescentParse(ParseNodeStream ts, TokenCategory delimiter, SyntaxClass rootClass) {
        ts.checkPoint();
        if (ts.remainingCount() == 1 || (
                        delimiter != null &&
                        ts.remainingCount() > 1 &&
                        ts.peek(ts.getIndex() + 1).isTerminal() &&
                        ts.peek(ts.getIndex() + 1).getTokenCategory().getTokenClassName().equals(delimiter.getTokenClassName()))) {
            if (!ts.peek().isTerminal() && ts.peek().getSyntaxClass().isOrExtends(rootClass)) {
                try {
                    ParseTreeNode ptn = ts.getNext();
                    ts.commit();
                    return ptn;
                } catch (ParseNodeStream.NoEnoughElementsException e) {
                    ts.rollback();
                    throw newParseFailedException(Collections.emptyList());
                }
            }
        }
        List<ParseTreeNode> foundSequence = new ArrayList<>();
        boolean foundSomething = true;
        while (foundSomething && !ts.isEmpty() &&
                        (delimiter == null ||
                        !ts.peek().isTerminal() ||
                        !ts.peek().getTokenCategory().getTokenClassName().equals(delimiter.getTokenClassName()))) {
            foundSomething = false;
            ParseTreeNode first = ts.peek();
            //TODO: something wrong happens when (2)+1, after evaluating "(2)", he finds a "plus" and there are no candidates starting with "plus"
            //TODO: must this be: candidatesStartingWith(<allTheContentsOfFoundSequence>, etc...)?
            //TODO: or (maybe instead of using a "foundSequence") i have to repush to the stream the nodes just found?
            //TODO: maybe here i should consider priority and associativity.
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

                        if (i == 0) { //todo: move to private method
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
                                        ParseNodeStream pns = ts.subStreamUntilDelimiter(nextComponent);
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
                        } else if (i == structure.size() - 1) { //todo: move to private method
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
                        } else { //todo: move to private method
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
                } catch (ParseNodeStream.NoEnoughElementsException e) {
                    found = false;
                }
                if (found) {
                    foundSomething = true;
                    ts.commit();
                    ParseTreeNode ptn = newNonTerminalNode(candidate.getBelongingClass(), candidate, tmpSequence);
                    foundSequence.add(ptn);
                } else {
                    ts.rollback();
                }
            }

        }
        if(!foundSomething && !foundSequence.isEmpty()){
            foundSequence.addAll(ts.subStreamUntilDelimiter(delimiter).toList()); //BUG TROVATO! subStreamUntilDelimiter non tiene conto del bilanciamento
            if(delimiter!=null) {
                foundSequence.add(ts.peek()); //adds the eventual delimiter without removing it from the original stream
            }
        }else if(!foundSomething){
            ts.rollback();
            throw newParseFailedException(Collections.emptyList());
        }
        if (foundSequence.size() == 1 && foundSequence.get(0).getSyntaxClass().isOrExtends(rootClass)) {
            ts.commit();
            return foundSequence.get(0);
        } else {
            try {
                ParseTreeNode ptn = recursiveDescentParse(new ParseNodeStream(foundSequence), delimiter, rootClass);
                ts.commit();
                return ptn;
            } catch (ParseFailedException e) {
                ts.rollback();
                throw e;
            } /*catch (ParseNodeStream.NoEnoughElementsException e) {
                ts.rollback();
                throw newParseFailedException(Collections.emptyList()); //TODO put a better found sequence
            }*/
        }
    }

    private boolean findUniqueComponent(ParseNodeStream ts, List<ParseTreeNode> tmpSequence, SyntaxCaseComponent component) throws ParseNodeStream.NoEnoughElementsException {
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
        ParseNodeStream ts = new ParseNodeStream(promoteHighPriorityTerminals(generateListFromToken(tokens, PARSE_TREE_NODE_FACTORY)));
        return recursiveDescentParse(ts, null, rootClass);
    }

    public class InvalidSyntaxCaseDefinitionException extends RuntimeException {
    }
}
