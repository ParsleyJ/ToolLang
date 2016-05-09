package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.InvalidConditionalExpressionException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.*;
import com.parsleyj.tool.objects.classes.ToolClass;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.special.ToolSemanticMethod;
import com.parsleyj.tool.semantics.MethodCall;
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
    protected MethodTable thisMethodTable = new MethodTable();
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

    public boolean evaluateAsConditional(Memory memory) throws ToolNativeException {
        try {
            MethodCall mc = new MethodCall(ToolSemanticMethod.METHOD_CATEGORY_SPECIAL, this, "asCondition", new RValue[]{});
            ToolObject returnedVal = mc.evaluate(memory);
            if(returnedVal.getBelongingClass().isOrExtends(BaseTypes.C_BOOLEAN)){
                return returnedVal.evaluateAsConditional(memory);
            }else{
                throw new InvalidConditionalExpressionException("Method "+mc+" does not return an object of a type that is or extends Boolean");
            }
        } catch (MethodNotFoundException e) {
            throw new InvalidConditionalExpressionException("The object "+this+" must have semantic asCondition defined or be of type Boolean");
        }
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

    public MethodTable generateCallableMethodTable(){
        if(this.getBelongingClass()!= null){
            return this.getBelongingClass().generateCallableMethodTable().extend(this.thisMethodTable);
        }else{
            return thisMethodTable;
        }
    }
}
