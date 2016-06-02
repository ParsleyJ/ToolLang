package com.parsleyj.tool.semantics.parameter;

import com.parsleyj.tool.exceptions.InvalidTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.tool.semantics.base.RValue;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ExplicitTypeParameterDefinition implements ParameterDefinition {

    private final Identifier name;
    private final RValue typeExpression;

    public ExplicitTypeParameterDefinition(Identifier name, RValue typeExpression){
        this.name = name;
        this.typeExpression = typeExpression;
    }

    @Override
    public FormalParameter defineParameter(Memory memory) throws ToolNativeException {
        return getFormalParameter(memory, typeExpression, name.getIdentifierString());
    }

    @NotNull
    public static FormalParameter getFormalParameter(Memory memory,
                                                     RValue typeExpression,
                                                     String name) throws ToolNativeException {
        ToolObject type = typeExpression.evaluate(memory);
        if (type instanceof ToolType){
            return new FormalParameter(name, (ToolType) type);
        }else if(type.respondsToInterface(memory.baseTypes().I_TYPE)) {
            return new FormalParameter(name, new ToolTypeAdapter(memory, type));
        }else{
            throw new InvalidTypeException(memory, "'"+typeExpression+"' is not a valid parameter type");
        }
    }
}
