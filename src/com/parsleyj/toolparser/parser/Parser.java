package com.parsleyj.toolparser.parser;

import com.parsleyj.toolparser.tokenizer.Token;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Parser} class. A parser object implements the mechanics to parse
 * a sequence of {@link Token}s in order to create a parse tree, using
 * the grammar rules defined in the {@link Grammar} instance assigned
 * to that parser with the constructor.
 */
public class Parser {

    private static final ParseTreeNodeFactory DEFAULT_STN_FACTORY = new ParseTreeNodeFactory();

    private Grammar grammar;

    /**
     * Creates a new {@link Parser} with the specified grammar rules.
     * @param grammar
     */
    public Parser(Grammar grammar) {
        this.grammar = grammar;
    }

    private List<ParseTreeNode> generateListFromToken(List<Token> tokens, ParseTreeNodeFactory stf){
        List<ParseTreeNode> treeList = new ArrayList<>();
        for (Token t: tokens) {
            TokenCategory tokenCategory = grammar.getTokenClass(t);
            if(tokenCategory == null) throw new InvalidTokenFoundException(t);
            ParseTreeNode ast = stf.newNodeTree();
            ast.setParsedToken(t);
            ast.setTokenCategory(tokenCategory);
            ast.setTerminal(true);
            treeList.add(ast);
        }
        return treeList;
    }



    public Pair<SyntaxCase, List<List<ParseTreeNode>>> findRoot(List<ParseTreeNode> sequence, SyntaxClass rootClass){
        for(int i = rootClass.getSyntaxCases().size()-1; i>=0; --i){
            SyntaxCase syntaxCase = rootClass.getSyntaxCases().get(i);
            List<List<ParseTreeNode>> checkResult = findRootByCase(sequence, syntaxCase);
            if (checkResult != null) {
                //that's the one
                return new Pair<>(syntaxCase, checkResult);
            }
        }
        return null;
    }

    private List<List<ParseTreeNode>> findRootByCase(List<ParseTreeNode> sequence, SyntaxCase syntaxCase){
        SyntaxCase.ParsingDirection pd = syntaxCase.getParsingDirection();



        Set<int[]> previousMatches = new HashSet<>();
        while(true){
            //find a "symbol presence" occurrence by scanning in the opposite direction of pd and exc
            int[] symMatch = findPotentialRoot(sequence, syntaxCase, pd, previousMatches);

            //if a new potential root is found
            if(symMatch != null){
                previousMatches.add(symMatch);
                //split the sequence in sub-sequences
                List<List<ParseTreeNode>> subSequences = getSubSequences(sequence, symMatch);
                boolean found = !subSequences.isEmpty();
                //check if the sequences are "well formed"
                for(List<ParseTreeNode> subSequence:subSequences){
                    if(!isWellFormed(subSequence)){
                        found = false;
                        break;
                    }
                }
                if(found){
                    //if they are, return them
                    return subSequences;
                }
                //...else continue with the next potential root

            }
            else return null;
        }
    }

    private boolean isWellFormed(List<ParseTreeNode> subSequence) {
        if(subSequence.isEmpty()) return true;
        try {
            priorityBasedParse(subSequence);
        }catch (ParseFailedException e){
            return false;
        }
        return true;
    }

    private List<List<ParseTreeNode>> getSubSequences(List<ParseTreeNode> sequence, int[] symbolPresences) {
        List<List<ParseTreeNode>> result = new ArrayList<>();
        int start = 0;
        int end;
        for(int symbolPresence : symbolPresences) {
            end = symbolPresence;

            if(end > start) result.add(sequence.subList(start, end));

            List<ParseTreeNode> sym = new ArrayList<>();
            sym.add(sequence.get(end));
            result.add(sym);

            start = end+1;
        }
        if(start < sequence.size()) result.add(sequence.subList(start, sequence.size()));
        return result;
    }

    private int[] initNegativeArr(int dim){
        int[] result = new int[dim];
        for(int i = 0; i< dim; ++i) result[i] = -1;
        return result;
    }



    private int[] findPotentialRoot(List<ParseTreeNode> sequence, SyntaxCase sc, SyntaxCase.ParsingDirection pd, Set<int[]> previousMatches) {
        switch (pd){
            case LeftToRight:{
                int[] indexes = initNegativeArr(sc.getTerminalSymbols().size());
                while (previousMatches.contains(indexes)){
                    List<SyntaxCaseComponent> syntaxCaseStructure = sc.getStructure();
                    //todo continue
                }
            }break;
            case RightToLeft:{
                //todo continue
            }break;
        }
        return null;
    }

    private static boolean subStructuralMatch()

    public ParseTreeNode recursiveParse(List<ParseTreeNode> sequence, SyntaxClass rootClass){
        List<Pair<SyntaxClass, SyntaxCase>> caseList = grammar.getPriorityCaseList();
        ParseTreeNodeFactory stf = DEFAULT_STN_FACTORY;

        SyntaxCase instanceCase = new SyntaxCase("", sequence.stream()
                .map(a -> a.isTerminal() ? a.getTokenCategory() : new SyntaxParsingInstance(a.getSyntaxClass(), a.getSyntaxCase()))
                .collect(Collectors.toList()).toArray(new SyntaxCaseComponent[sequence.size()]));
        for(Pair<SyntaxClass, SyntaxCase> cas: caseList){
            if(Grammar.caseMatch(instanceCase, cas.getSecond())){
                ParseTreeNode nst = stf.newNodeTree(sequence.toArray(new ParseTreeNode[sequence.size()]));
                nst.setSyntaxClass(cas.getFirst());
                nst.setSyntaxCase(cas.getSecond());
                nst.setTerminal(false);
                return nst;
            }
        }
        Pair<SyntaxCase, List<List<ParseTreeNode>>> rootSearchResult = findRoot(sequence, rootClass);

        if(rootSearchResult == null) throw new RuntimeException("Parse failed!"); //todo change this

        List<ParseTreeNode> children = new ArrayList<>();
        SyntaxCase rootCase = rootSearchResult.getFirst();
        List<List<ParseTreeNode>> sublists = rootSearchResult.getSecond();
        int i = 0;
        for(SyntaxCaseComponent c: rootCase.getStructure()){
            if(c instanceof SyntaxClass){
                ParseTreeNode n = recursiveParse(sublists.get(i++), (SyntaxClass) c);
                children.add(n);
            }else if (c instanceof TokenCategory){
                children.add(sublists.get(i++).get(0));
            }else if (c instanceof SpecificCaseComponent){
                ParseTreeNode n = recursiveParse(sublists.get(i++), ((SpecificCaseComponent) c).getClas());
                children.add(n);
            }else{
                //todo error?
            }
        }
        ParseTreeNode result = stf.newNodeTree(children.toArray(new ParseTreeNode[children.size()]));
        result.setSyntaxClass(rootClass);
        result.setSyntaxCase(rootCase);
        result.setTerminal(false);
        return result;
    }


    public ParseTreeNode parse(List<Token> tokens){
        return priorityBasedParse(generateListFromToken(tokens, DEFAULT_STN_FACTORY));
    }

    /**
     * Iterative parsing algorithm. At each iteration, it tries to find a sub-sequence
     * of {@link ParseTreeNode}s that matches a {@link SyntaxCase}, using the
     * {@link Grammar#caseMatch(SyntaxCase, SyntaxCase)} method. This algorithm tries
     * to find the {@link SyntaxCase}s in the same order defined in the {@link Grammar},
     * in this way it can respect the defined precedence rules. When, in a iteration,
     * the parser finds a matching candidate sub-sequence, creates a new
     * {@link ParseTreeNode} with the members of the sub-sequence as children and inserts
     * it in the original sequence instead of that subsequence. If the parsing operation
     * is successful, the result will be a {@link ParseTreeNode} containing the whole parse
     * tree. The algorithm fails throwing a {@link ParseFailedException} when during an
     * iteration all the attempts to find a successful match fail and there are more than
     * one {@link ParseTreeNode}s in the sequence.
     * @param treeList the list of a sequence of
     * @return a {@link ParseTreeNode} which is the root of the parse tree.
     */
    public ParseTreeNode priorityBasedParse(List<ParseTreeNode> treeList){
        List<Pair<SyntaxClass, SyntaxCase>> caseList = grammar.getPriorityCaseList();
        while(true){
            boolean lastIterationFailed = true;
            if(treeList.size() <= 1) break;

            for (int iCase = 0; iCase < caseList.size(); iCase++) {
                Pair<SyntaxClass, SyntaxCase> currentCase = caseList.get(iCase);
                List<ParseTreeNode> tempList = new ArrayList<>();
                int window = currentCase.getSecond().getStructure().size();
                int start = 0;
                while (start <= treeList.size() - window) {
                    //todo implement reverse scan for different associativity?
                    int end = start + window;
                    List<ParseTreeNode> currentSubList = treeList.subList(start, end);
                    SyntaxCase instanceCase = new SyntaxCase("", currentSubList.stream()
                            .map(a -> a.isTerminal() ? a.getTokenCategory() : new SyntaxParsingInstance(a.getSyntaxClass(), a.getSyntaxCase()))
                            .collect(Collectors.toList()).toArray(new SyntaxCaseComponent[currentSubList.size()]));
                    boolean caseMatch = Grammar.caseMatch(instanceCase, currentCase.getSecond());
                    if (!caseMatch) {
                        tempList.add(currentSubList.get(0));
                    } else {
                        lastIterationFailed = false;

                        ParseTreeNode nst = DEFAULT_STN_FACTORY.newNodeTree(currentSubList.toArray(new ParseTreeNode[currentSubList.size()]));
                        nst.setSyntaxClass(currentCase.getFirst());
                        nst.setSyntaxCase(currentCase.getSecond());
                        nst.setTerminal(false);
                        tempList.add(nst);

                        start += window - 1;
                        //force to scan the rest of the sequence from the first syntax case again.
                        iCase = 0;
                        currentCase = caseList.get(iCase);
                    }
                    ++start;
                }

                //there are no more subLists with this window at this start index
                //we need to add the remaining elements to tempList
                for (int i = start; i < treeList.size(); ++i)
                    tempList.add(treeList.get(i));

                treeList = tempList;
            }

            if(lastIterationFailed) break;
        }


        if(treeList.size() == 1){
            return treeList.get(0);
        } else {
            ParseTreeNode errorNode = DEFAULT_STN_FACTORY.newNodeTree(treeList.toArray(new ParseTreeNode[treeList.size()]));
            errorNode.setSyntaxCase(new SyntaxCase("PARSE FAILED"));
            errorNode.setSyntaxClass(new SyntaxClass("###"));
            throw new ParseFailedException(errorNode);
        }
    }








}
