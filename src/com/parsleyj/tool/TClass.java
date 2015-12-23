package com.parsleyj.tool;

import java.util.*;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TClass extends TObject {

    private String name;

    private Map<String, TMethod> methods = new HashMap<String, TMethod>();

    private Map<String, TField> fields = new HashMap<String, TField>();
    private TClass parentClass;

    private List<TLiteral> literalClasses = new ArrayList<TLiteral>();

    public TClass(String name, TClass parentClass) {
        this.name = name;
        this.parentClass = parentClass;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public TClass getTClass() {
        return TBaseTypes.CLASS_CLASS;
    }

    public TMethod getClassMethod(String completeName) throws MethodNotFoundException {
        TClass tmp = this;

        while (tmp != null) {
            TMethod tmpMethod = methods.get(completeName);
            if (tmpMethod != null) return tmpMethod;
            tmp = tmp.getParentClass();
        }

        throw new MethodNotFoundException();
    }

    @Override
    public String getStringRepresentation() {
        return "<" + name + ">";
    }

    public String getClassName() {
        return name;
    }


    public void addClassMethod(TMethod tMethod) {
        methods.put(tMethod.getCompleteName(), tMethod);
    }

    public void addClassField(TField tField){
        fields.put(tField.getFieldName().getIdentifierString(), tField);
    }

    public TField getClassField(String name){
        return fields.get(name);
    }

    public Collection<TField> getClassFields(){
        return fields.values();
    }

    public TObject newInstance(TObject... constructorParameters) {
        return new TObject(this); //TODO: constructorParameters
    }

    public TClass getParentClass() {
        return parentClass;
    }

    public List<TLiteral> getILiterals() {
        return literalClasses;
    }

    public void addLiteralClass(TLiteral tLiteral) {
        literalClasses.add(tLiteral);
    }

    public boolean isOrExtends(TClass tClass) {
        TClass tmp = this;

        while (tmp != null) {
            if (tmp.isSameIstance(tClass)) return true;
            tmp = tmp.getParentClass();
        }

        return false;
    }

    public class MethodNotFoundException extends Exception {
    }
}
