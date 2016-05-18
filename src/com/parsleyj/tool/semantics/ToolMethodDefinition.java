package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ToolMethodDefinition implements RValue{

    private final Identifier identifier;
    private final RValue body;
    private final List<ParameterDefinition> params;

    public ToolMethodDefinition(Identifier identifier, RValue body){
        this.identifier = identifier;
        this.params = new ArrayList<>();
        this.body = body;
    }

    public ToolMethodDefinition(Identifier identifier, List<ParameterDefinition> params, RValue body) {
        this.identifier = identifier;
        this.params = params;
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        List<FormalParameter> formalParameters = new ArrayList<>();
        for (ParameterDefinition param : params) {
            formalParameters.add(param.define(memory));
        }
        ToolMethod method = new ToolMethod(
                Visibility.Public,
                identifier.getIdentifierString(),
                new FormalParameter[]{},
                formalParameters.toArray(new FormalParameter[formalParameters.size()]),
                new ToolBoolean(true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }
}
