package com.parsleyj.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TObject {

    private static class IdProvider {
        private static int idCounter = 0;

        private static int getNextId() {
            ++idCounter;
            return idCounter;
        }
    }

    private int id = IdProvider.getNextId();

    protected Object primitiveValue;

    protected TClass belongingClass;

    private Map<String, TMethod> instanceMethods = new HashMap<String, TMethod>();//TODO: should this be in memory?

    private ToolMemory toolMemory;

    public TObject() {
        belongingClass = TBaseTypes.OBJECT_CLASS;
        toolMemory = new ToolMemory(this);
    }

    public TObject(TClass belongingClass) {
        this.belongingClass = belongingClass;
        toolMemory = new ToolMemory(this);
    }

    public TObject(TClass belongingClass, Object primitiveValue) {
        this.primitiveValue = primitiveValue;
        this.belongingClass = belongingClass;
        toolMemory = new ToolMemory(this);
    }

    public TClass getTClass() {
        return belongingClass;
    }

    public String getStringRepresentation() {
        return null;//TODO: impl
    }

    public String getObjectIdName() {
        return getTClass().getClassName() + "#" + getId();
    }

    public TReference getField(TField field) {
        return null;//TODO: impl
    }

    public TObject callMethod(String name, TObject... params) throws TClass.MethodNotFoundException {
        String completeName = TMethod.getCompleteNameFromActual(name, params);
        TMethod m = instanceMethods.get(completeName); //TODO: maybe just a string check is not enough (inheritance)
        if (m != null) return m.evaluate(this, params); //TODO: push a stack
        else return getTClass().getMethod(name).evaluate(this, params);
    }

    public void addInstanceMethod(TMethod method) {
        instanceMethods.put(method.getCompleteName(), method);
    }

    public Object getPrimitiveValue() {
        return primitiveValue;
    }

    public boolean isSameIstance(TObject object) {
        return object.id == this.id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getObjectIdName();
    }

    //override this for null object
    public boolean isNullValue() {
        return false;
    }


    public boolean isClass() {
        return false;
    }

    public ToolMemory getNamespace() {
        return toolMemory;
    }
}
