package com.parsleyj.toolparser.parser;

import com.parsleyj.toolparser.tokenizer.TokenCategory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a syntax case. For example, in this simple
 * grammar:
 *      {@code Exp:= n | (E) | (E + E) | (E * E)}
 * Each one of the expressions separated by the {@code | } is represented
 * by a {@link SyntaxCase}.
 */
public class SyntaxCase{

    private String caseName;
    private List<SyntaxCaseComponent> structure;
    private Associativity associativity = Associativity.LeftToRight;
    private SyntaxClass belongingClass;
    /**
     * Creates a new syntax case with the given name and structure.
     * @param caseName the name of this case.
     * @param structure a varargs list of case components.
     */
    public SyntaxCase(String caseName, SyntaxClass belongingClass, SyntaxCaseComponent... structure) {
        this.caseName = caseName;
        this.structure = Arrays.asList(structure);
        this.belongingClass = belongingClass;
    }


    /**
     * @return the structure of this syntax case.
     */
    public List<SyntaxCaseComponent> getStructure() {
        return structure;
    }

    /**
     * @return the name of this case
     */
    public String getCaseName() {
        return caseName;
    }

    /**
     * @param caseName the the name of this case.
     */
    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }


    public SyntaxClass getBelongingClass() {
        return belongingClass;
    }

    public void setBelongingClass(SyntaxClass belongingClass) {
        this.belongingClass = belongingClass;
    }

    public void setStructure(List<SyntaxCaseComponent> structure) {
        this.structure = structure;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public SyntaxCase setAssociativity(Associativity associativity) {
        this.associativity = associativity;
        return this;
    }

    public List<TokenCategory> getTerminalSymbols(){
        return structure.stream()
                .filter(syntaxCaseComponent -> syntaxCaseComponent instanceof TokenCategory)
                .map(syntaxCaseComponent -> (TokenCategory) syntaxCaseComponent)
                .collect(Collectors.toList());
    }
}
