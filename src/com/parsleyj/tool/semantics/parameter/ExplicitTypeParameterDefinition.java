package com.parsleyj.tool.semantics.parameter;

import com.parsleyj.tool.exceptions.InvalidParameterTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolInterface;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.ParameterDefinition;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ExplicitTypeParameterDefinition implements ParameterDefinition {

    private final Identifier name;
    private final Identifier typeExpression;

    public ExplicitTypeParameterDefinition(Identifier name, Identifier typeExpression){
        this.name = name;
        this.typeExpression = typeExpression;
    }

    @Override
    public FormalParameter defineParameter(Memory memory) throws ToolNativeException {
        ToolObject type = typeExpression.evaluate(memory);
        if(type.getBelongingClass().isOrExtends(BaseTypes.C_CLASS)) {
            return new FormalParameter(name.getIdentifierString(), (ToolClass) type);
        }else if(type.getBelongingClass().isOrExtends(BaseTypes.C_INTERFACE)){
            return new FormalParameter(name.getIdentifierString(), (ToolInterface) type);
        }else{
            throw new InvalidParameterTypeException("'"+typeExpression+"' is not a valid type");
        }
    }
}
