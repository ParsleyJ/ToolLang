package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolType;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.flowcontrol.ReturnStatement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */

public class ToolMethod extends ToolMethodPrototype{

    public static final String METHOD_TAG = "method";

    public static final String METHOD_CATEGORY_METHOD = "METHOD_CATEGORY_METHOD";

    private Visibility visibility;
    private List<String> argumentNames = new ArrayList<>();
    private RValue condition;
    private RValue body;
    private ArrayDeque<Memory.Scope> definitionScope = new ArrayDeque<>();
    private ToolObject ownerObject;
    private List<String> tags = new ArrayList<>();

    public ToolMethod(Memory m, Visibility visibility, String name,
                      FormalParameter[] parameters, RValue body) {
        super(m, m.baseTypes().C_METHOD);
        this.methodCategory = METHOD_CATEGORY_METHOD;
        this.visibility = visibility;
        this.name = name;
        for (FormalParameter parameter : parameters) {
            this.getArgumentTypes().add(parameter.getParameterType());
            this.argumentNames.add(parameter.getParameterName());
        }

        this.condition = new ToolBoolean(m, true);
        this.body = body;
        this.ownerObject = m.getSelfObject();
    }

    public ToolMethod(Memory m, Visibility visibility, String name,
                      FormalParameter[] parameters,
                      RValue condition, RValue body) {
        super(m, m.baseTypes().C_METHOD);
        this.methodCategory = METHOD_CATEGORY_METHOD;
        this.visibility = visibility;
        this.name = name;
        for (FormalParameter parameter : parameters) {
            this.getArgumentTypes().add(parameter.getParameterType());
            this.argumentNames.add(parameter.getParameterName());

        }

        this.condition = condition;
        this.body = body;
        this.ownerObject = m.getSelfObject();
    }

    public ToolMethod(Memory m, String methodCategory, Visibility visibility, String name,
                      FormalParameter[] parameters,
                      RValue body){
        this(m, visibility, name, parameters, body);
        this.methodCategory = methodCategory;
    }

    public ToolMethod(Memory m, String methodCategory, Visibility visibility, String name,
                      FormalParameter[] parameters,
                      RValue condition, RValue body){
        this(m, visibility, name, parameters, condition, body);
        this.methodCategory = methodCategory;
    }

    public void addTag(String s){
        tags.add(s);
    }

    public List<String> getTags() {
        return tags;
    }

    public ToolObject call(Memory memory,
                           ToolObject caller,
                           List<ToolObject> arguments,
                           ArrayDeque<Memory.Scope> definitionScope) throws ToolNativeException {
        ToolObject result;
        memory.pushCallFrame(caller, definitionScope);
        tags.forEach(t -> memory.getCurrentFrame().addTag(t));

        for (int i = 0; i < getArgumentNames().size(); ++i) {
            memory.newLocalReference(getArgumentNames().get(i), arguments.get(i));
            memory.getTopScope().getNameTable().put(getArgumentNames().get(i), Memory.NameKind.Variable);
        }

        try {
            result = getBody().evaluate(memory);
        }catch (ToolNativeException tne) {
            tne.addFrameToTrace("\tat: "+ completeFunctionName());
            memory.returnFromCallError();
            throw tne;
        }catch (ReturnStatement.Return e){
            if (memory.getCurrentFrame().containsTag(e.getTag())) {
                memory.returnFromCall();
                return e.getResult();
            } else {
                memory.returnFromCall();
                throw e;
            }
        }
        memory.returnFromCall();
        return result;
    }

    public RValue getCondition() {
        return condition;
    }

    public RValue getBody() {
        return body;
    }


    public List<String> getArgumentNames() {
        return argumentNames;
    }






    @Override
    public Visibility getVisibility() {
        return visibility;
    }


    public String completeInstanceMethodName(ToolClass self) throws ToolNativeException {//TODO: change for categories
        StringBuilder sb = new StringBuilder("<"+self.getClassName()+">."+ getMethodName() +"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    public String completeClassMethodName(ToolClass self) throws ToolNativeException {//TODO: change for categories
        StringBuilder sb = new StringBuilder(self.getClassName()+"."+ getMethodName() +"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    public String completeFunctionName() throws ToolNativeException {//TODO: change for categories
        StringBuilder sb = new StringBuilder(getMethodName() +"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    private void addParameterListToStringBuilder(StringBuilder sb) throws ToolNativeException {
        for (int i = 0; i < getArgumentTypes().size(); i++) {
            ToolType argumentType = getArgumentTypes().get(i);
            sb.append("<").append(argumentType.getTypeName()).append(">");
            if(i < getArgumentTypes().size()-1) sb.append(", ");
        }
    }

    public void putDefinitionScope(ArrayDeque<Memory.Scope> definitionScope) {
        for (Memory.Scope scope : definitionScope) {
            this.definitionScope.add(scope);
        }
    }

    public ArrayDeque<Memory.Scope> getDefinitionScope(){
        return definitionScope;
    }

    public ToolObject getOwnerObject() {
        return ownerObject;
    }


    /*public static ToolObject callOperator(@MemoryParameter Memory memory,
                                            @SelfParameter ToolMethod self,
                                            ToolList parameters){
        return self.call(memory, new ArrayList<>(), parameters.getToolObjects(), self.definitionScope, self, )
        //FIXME: problem: forcing a call on a resolved method does not push information about the owner object
    }*/

}
