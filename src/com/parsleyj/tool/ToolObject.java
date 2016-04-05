package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolObject implements RValue {
    private int referenceCount = 0;

    public Object getNativeValue() {
        return nativeVal;
    }

    public boolean isNull() {
        return false;
    }

    private static class IDGenerator {

        private static int id = 0;
        public static int generate() {
            return id++;
        }

    }
    private ToolClass belongingClass;

    private Integer id = IDGenerator.generate();
    private Object nativeVal = null;
    private Scope scope = new Scope();
    public ToolObject() {
        this.belongingClass = BaseTypes.C_OBJECT;
    }

    public ToolObject(ToolClass belongingClass) {
        this.belongingClass = belongingClass;
    }

    public ToolObject(ToolClass belongingClass, Object nativeVal) {
        this.belongingClass = belongingClass;
        this.nativeVal = nativeVal;
    }

    public void addReferenceMember(Reference reference) {
        this.scope.putReference(reference);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        return this;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void increaseReferenceCount() {
        ++this.referenceCount;
    }

    public void decreaseReferenceCount() {
        --this.referenceCount;
    }

    public Reference getReferenceMember(String identifierString) {
        return scope.getReferenceByName(identifierString);
    }

    public ToolClass getBelongingClass() {
        return belongingClass;
    }

    public Integer getId() {
        return id;
    }

    public Object nativeValue() {
        return nativeVal;
    }

    @Override
    public String toString() {
        return "<"+(
                getBelongingClass() == null ?
                        "NULL" :
                        (getBelongingClass().getClassName()+":"+(
                                getNativeValue() == null?
                                        "":
                                        getNativeValue())))
                +"@id:"+getId()+">";
    }

    public String getPrintString() {
        return toString();
    }
}
