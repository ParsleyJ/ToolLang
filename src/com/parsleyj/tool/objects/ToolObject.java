package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.*;
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


    private ToolClass belongingClass;
    private Integer id = IDGenerator.generate();
    private Memory.Scope scope;
    protected Memory memory;


    public ToolObject(Memory m) {
        this.memory = m;
        this.belongingClass = m.baseTypes().C_OBJECT;
        scope = new Memory.Scope(m, Memory.Scope.ScopeType.Object);
    }
    public ToolObject(Memory m, ToolClass belongingClass) {
        this.memory = m;
        this.belongingClass = belongingClass;
        scope = new Memory.Scope(m, Memory.Scope.ScopeType.Object);
    }

    public void newMember(Reference reference) throws ReferenceAlreadyExistsException {
        this.scope.putReference(reference);
    }

    public void newMember(String name, ToolObject object) throws ReferenceAlreadyExistsException {
        this.scope.putReference(new Reference(name, object));
    }

    public void updateMember(String name, ToolObject object) throws ReferenceNotFoundException {
        Reference ref = this.scope.getReferenceByName(name);
        if(ref == null) throw new ReferenceNotFoundException(memory, "Reference with name: " + name + " not found.");
        ref.setValue(object);
    }

    public Reference getReferenceMember(String identifierString) throws ReferenceNotFoundException {
        Reference referenceByName = scope.getReferenceByName(identifierString);
        if (referenceByName == null) {
            throw new ReferenceNotFoundException(scope.getBelongingMemory(), "Reference with name: "+identifierString+" not found.");
        }
        return referenceByName;
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



    public void addMethod(ToolMethod method) throws AmbiguousMethodDefinitionException {
        scope.getMethods().add(method);
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
