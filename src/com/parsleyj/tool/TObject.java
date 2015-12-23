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

    public static final THeapMemory BASE_MEMORY = new THeapMemory();

    private int id = IdProvider.getNextId();

    protected Object primitiveValue;

    protected TClass belongingClass;

    private Map<String, TMethod> instanceMethods = new HashMap<String, TMethod>();//TODO: should this be in memory? (let it be <str, int>?)

    private TNamespace namespace;

    public TObject() {
        belongingClass = TBaseTypes.OBJECT_CLASS;
        namespace = new TNamespace(this, BASE_MEMORY);
    }

    public TObject(TClass belongingClass) {
        this.belongingClass = belongingClass;
        namespace = new TNamespace(this, BASE_MEMORY);
    }

    public TObject(TClass belongingClass, Object primitiveValue) {
        this.primitiveValue = primitiveValue;
        this.belongingClass = belongingClass;
        namespace = new TNamespace(this, BASE_MEMORY);
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
        namespace.getFirstReferenceInStack(field.getFieldName().getIdentifierString());
        return null;//TODO: impl
    }

    public TObject callMethod(TIdentifier name, TObject... params) {
        String completeName = TMethod.getCompleteNameFromActual(name.getIdentifierString(), params);
        //first, searches in instance methods
        TMethod m = instanceMethods.get(completeName);
        if (m != null){
            namespace.pushNewStack();
            TObject result = m.invoke(this, params);
            namespace.popStack();
            return result;
        } else { //if nothing was found, search in methods defined in belonging class
            try {
                namespace.pushNewStack();
                TObject result = getTClass().getClassMethod(completeName).invoke(this, params);
                namespace.popStack();
                return result;
            }catch (TClass.MethodNotFoundException e){
                namespace.popStack();
                return InternalUtils.throwError(TBaseTypes.METHOD_NOT_FOUND_ERROR_CLASS,
                        "No method '" + completeName + "' found in object " + this + " ."
                );
            }

        }
    }

    public void addInstanceMethod(TMethod method) {
        instanceMethods.put(method.getCompleteName(), method);
    }

    public Map<String, TMethod> getInstanceMethods() {
        return instanceMethods;
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

    public TNamespace getNamespace() {
        return namespace;
    }
}
