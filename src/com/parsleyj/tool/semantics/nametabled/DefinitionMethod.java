package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.NameAlreadyUsedException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.flowcontrol.ReturnStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class DefinitionMethod implements RValue {

    private final Identifier identifier;
    private final RValue body;
    private final List<ParameterDefinition> params;
    private final List<String> methodTags = new ArrayList<>();

    public DefinitionMethod(Identifier identifier, RValue body){
        this.identifier = identifier;
        this.params = new ArrayList<>();
        this.body = body;
    }

    public DefinitionMethod(Identifier identifier, List<ParameterDefinition> params, RValue body) {
        this.identifier = identifier;
        this.params = params;
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        String name = identifier.getIdentifierString();
        Memory.NameKind nameKind = memory.getTopScope().getNameTable().get(name);
        if(nameKind == null){
            memory.getTopScope().getNameTable().put(name, Memory.NameKind.Method);
            return createAndAddMethod(memory, name, params, body);
        }else switch (nameKind){
            case Variable:
            case Accessor:
            case VariableAndAccessor:
                throw new NameAlreadyUsedException(memory,
                        "Cannot define method: '"+name+"' is an already used name in this scope.");
            case Method:
                return createAndAddMethod(memory, name, params, body); //tries to overload
            default: //never goes here
                return null;
        }
    }

    @Override
    public void addTag(String s) {
        methodTags.add(s);
    }

    public ToolMethod createAndAddMethod(Memory memory, String name, List<ParameterDefinition> params, RValue body) throws ToolNativeException{
        List<FormalParameter> formalParameters = new ArrayList<>();
        for (ParameterDefinition param : params) {
            formalParameters.add(param.defineParameter(memory));
        }
        ToolMethod method = createMethod(memory, methodTags, name, formalParameters, body);

        method.putDefinitionScope(memory.getCurrentFrameStack());
        memory.getTopScope().addMethod(method);
        return method;
    }

    public static ToolMethod createMethod(Memory memory, List<String> methodTags, String name, List<FormalParameter> formalParameters, RValue body) {
        ToolMethod method = new ToolMethod(
                memory,
                Visibility.Public,
                name,
                formalParameters.toArray(new FormalParameter[formalParameters.size()]),
                new ToolBoolean(memory, true),
                body);
        method.addTag(ReturnStatement.RETURNABLE_CALL_FRAME_TAG);
        method.addTag(ToolMethod.METHOD_TAG);
        method.addTag(name);
        methodTags.forEach(method::addTag);
        return method;
    }
}
