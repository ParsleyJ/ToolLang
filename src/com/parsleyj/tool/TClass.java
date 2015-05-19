package com.parsleyj.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TClass extends TObject {

    private String name;

    private Map<String, TMethod> methods = new HashMap<String, TMethod>();

    private Map<String, TField> fields;
    private TClass parentClass;

    private List<TLiteral> literalClasses = new ArrayList<TLiteral>();

    public TClass(String name, TClass parentClass) {
        this.name = name;
        this.parentClass = parentClass;
    }

    @Override
    public TClass getTClass() {
        return TBaseTypes.CLASS_CLASS;
    }

    public TMethod getMethod(String name) throws MethodNotFoundException{
        TClass tmp = this;

        while (tmp != null){
            TMethod tmpMethod = methods.get(name);
            if(tmpMethod != null) return tmpMethod;
            tmp = tmp.getParentClass();
        }

        throw new MethodNotFoundException();
    }

    @Override
    public String getStringRepresentation() {
        return "<"+name+">";
    }

    public String getName() {
        return name;
    }


    public void addMethod(TMethod iMethod) {
        methods.put(iMethod.getCompleteName(), iMethod);
    }

    public TObject newInstance(TObject... constructorParameters){
        return new TObject(); //TODO: constructorParameters
    }

    public TClass getParentClass() {
        return parentClass;
    }

    public List<TLiteral> getILiterals(){
        return literalClasses;
    }

    public void addLiteralClass(TLiteral tLiteral){
        literalClasses.add(tLiteral);
    }

    public boolean isOrExtends(TClass tClass){
        TClass tmp = this;

        while(tmp != null){
            if(tmp.isSameIstance(tClass)) return true;
            tmp = tmp.getParentClass();
        }

        return false;
    }

    public class MethodNotFoundException extends Exception {
    }
}
