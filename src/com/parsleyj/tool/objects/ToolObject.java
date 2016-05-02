package com.parsleyj.tool.objects;

import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.AddedReference;
import com.parsleyj.tool.memory.CounterIsZeroRemoveObject;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Scope;
import com.parsleyj.tool.semantics.RValue;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolObject implements RValue {
    private int referenceCount = 0;


    private static class IDGenerator {

        private static int id = 0;
        public static int generate() {
            return id++;
        }

    }

    private ToolClass belongingClass;
    private Integer id = IDGenerator.generate();
    private Scope scope = new Scope(Scope.ScopeType.Object);
    public ToolObject() {
        this.belongingClass = BaseTypes.C_OBJECT;
    }

    public ToolObject(ToolClass belongingClass) {
        this.belongingClass = belongingClass;
    }


    public void addReferenceMember(Reference reference) {
        try {

            this.scope.putReference(reference);
        } catch (AddedReference addedReference) {
            //
        }
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        return this;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void increaseReferenceCount() {
        ++this.referenceCount;
    }

    public void decreaseReferenceCount() throws CounterIsZeroRemoveObject {
        --this.referenceCount;
        if(this.referenceCount <= 0) throw new CounterIsZeroRemoveObject();
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

    public Scope getMembersScope() {
        return scope;
    }

    public boolean isNull() {
        return false;
    }

    public void forceSetBelongingClass(ToolClass belongingClass) {
        this.belongingClass = belongingClass;
    }

    @Override
    public String toString() {
        return "<"+(
                getBelongingClass() == null ?
                        "NULL" :
                        getBelongingClass().getClassName()
                )
                +":"+"@id:"+getId()+">";
    }

    public String getPrintString() {
        return toString();
    }
}
