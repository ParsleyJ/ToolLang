package com.parsleyj.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TObject implements TExpression {

    private int id; //TODO: ids and references

    protected Object primitiveValue;

    protected TClass belongingClass;

    private Map<String, TMethod> instanceMethods = new HashMap<String, TMethod>();

    public TObject(){
        belongingClass = TBaseTypes.OBJECT_CLASS;
    }

    public TObject(TClass belongingClass){
        this.belongingClass = belongingClass;
    }

    public TObject(TClass belongingClass, Object primitiveValue){
        this.primitiveValue = primitiveValue;
        this.belongingClass = belongingClass;
    }

    public TClass getTClass() {
        return belongingClass;
    }

    public String getStringRepresentation() {
        return null;//TODO: impl
    }

    public TReference getField(TField field){
        return null;//TODO: impl
    }

    public TObject callMethod(String name, TObject... params) throws TClass.MethodNotFoundException {
        String completeName = TMethod.getCompleteNameFromActual(name, params);
        TMethod m = instanceMethods.get(completeName); //TODO: maybe just a string check is not enough (inheritance)
        if(m!=null) return m.evaluate(this, params); //TODO: push a stack
        else return getTClass().getMethod(name).evaluate(this, params);
    }

    public void addInstanceMethod(TMethod method){
        instanceMethods.put(method.getCompleteName(), method);
    }

    public Object getPrimitiveValue(){
        return primitiveValue;
    }

    public boolean isSameIstance(TObject object){
        return object.id == this.id;
    }

}
