package com.parsleyj.toolparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a specific syntax class. This class implements
 * {@link SyntaxCaseComponent}, so its instances can be used
 * as components to define syntax cases.
 */
public class SyntaxClass implements SyntaxCaseComponent {

    private String name;
    private SyntaxClass extendedClass = null;
    private List<SyntaxCase> cases;

    /**
     * Creates a new syntax class with the given name.
     * @param name the name of this class.
     */
    public SyntaxClass(String name) {
        this.name = name;
        cases = new ArrayList<>();
    }

    /**
     * Creates a new syntax class with the given name and
     * list of cases.
     * @param name the name of this class.
     * @param cases the cases that generate instances of this syntax class.
     */
    public SyntaxClass(String name, List<SyntaxCase> cases) {
        this.name = name;
        this.cases = cases;
    }

    //TODO: doc
    public SyntaxClass(String className, SyntaxClass extendedClass) {
        this.name = className;
        this.extendedClass = extendedClass;
        this.cases = new ArrayList<>();
    }

    //TODO: doc
    public boolean isDerivate(){
        return (extendedClass != null);
    }

    public SyntaxClass getExtendedClass(){
        return extendedClass;
    }


    /**
     * @return the cases that generate instances of this syntax class.
     */
    public List<SyntaxCase> getSyntaxCases() {
        return cases;
    }

    /**
     * @param cases the cases that generate instances of this syntax
     *              class.
     */
    public void setCases(SyntaxCase... cases) {
        this.cases = Arrays.asList(cases);
    }

    /**
     * Adds a case to the list of cases of this syntax class.
     * @param cas the cas to be added
     */
    public void addCase(SyntaxCase cas){
        if (this.cases == null) {
            cases = new ArrayList<>();
        }
        cases.add(cas);
    }

    @Override
    public String getSyntaxComponentName() {
        return name;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    public boolean isOrExtends(SyntaxClass c){
        SyntaxClass x = this;
        while(x != null){
            if(x.getSyntaxComponentName().equals(c.getSyntaxComponentName())) return true;
            x = x.getExtendedClass();
        }
        return false;
    }

}
