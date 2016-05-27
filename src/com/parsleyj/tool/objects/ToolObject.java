package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.exceptions.InvalidConditionalExpressionException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.*;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolObject implements RValue {


    private int referenceCount = 0;
    private ToolClass belongingClass;
    private Integer id = IDGenerator.generate();
    private Memory.Scope scope;


    public ToolObject(Memory m) {
        this.belongingClass = m.baseTypes().C_OBJECT;
        scope = new Memory.Scope(m, Memory.Scope.ScopeType.Object);
    }
    public ToolObject(Memory m, ToolClass belongingClass) {
        this.belongingClass = belongingClass;
        scope = new Memory.Scope(m, Memory.Scope.ScopeType.Object);
    }
    public void addReferenceMember(Reference reference) {
        try {
            this.scope.putReference(reference);
        } catch (AddedReference addedReference) {
            //
        }
    }

    public void writeObjectMember(String name, Memory memory, ToolObject object) {
        Reference oldR = getReferenceMember(name);
        if(oldR!=null){
            ToolObject old = memory.getObjectById(oldR.getPointedId());
            try {
                old.decreaseReferenceCount();
            } catch (CounterIsZeroRemoveObject counterIsZeroRemoveObject) {
                memory.removeObject(old.id);
            }
        }
        memory.addObjectToHeap(object);
        Reference r = new Reference(name, object);
        addReferenceMember(r);
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        return this;
    }

    public boolean evaluateAsConditional(Memory memory) throws ToolNativeException {
        try {
            MethodCall mc = MethodCall.getter(this, "asCondition");
            ToolObject returnedVal = mc.evaluate(memory);
            if(returnedVal.getBelongingClass().isOrExtends(memory.baseTypes().C_BOOLEAN)){
                return returnedVal.evaluateAsConditional(memory);
            }else{
                throw new InvalidConditionalExpressionException(memory, "Method "+mc+" does not return an object of a type that is or extends Boolean");
            }
        } catch (MethodNotFoundException e) {
            throw new InvalidConditionalExpressionException(memory, "The object "+this+" must have semantic asCondition defined or be of type Boolean");
        }
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void increaseReferenceCount() {
        ++this.referenceCount;
    }

    public void addMethod(ToolMethod method) throws AmbiguousMethodDefinitionException {
        scope.getMethods().add(method);
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

    public Memory.Scope getMembersScope() {
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

    public MethodTable generateCallableMethodTable(){
        if(this.getBelongingClass()!= null && this != this.getBelongingClass()){
            return this.getBelongingClass().generateInstanceCallableMethodTable().extend(this.scope.getMethods());
        }else{
            return this.scope.getMethods();
        }
    }

    /**
     * Called by Memory right before being destroyed
     */
    public void onDestroy(Memory memory){
        //does nothing here
    }


    private static class IDGenerator {

        private static int id = 0;

        public static int generate() {
            return id++;
        }

    }
}
