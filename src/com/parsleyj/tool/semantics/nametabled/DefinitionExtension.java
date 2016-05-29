package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.HashMap;

/**
 * Created by Giuseppe on 27/05/16.
 * TODO: javadoc
 */
public class DefinitionExtension implements RValue{

    private RValue klassExpression;
    private RValue body;

    public DefinitionExtension(RValue body, RValue klassExpression) {
        this.body = body;
        this.klassExpression = klassExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolExtensor extensor = (ToolExtensor) new DefinitionExtensor(body).evaluate(memory);
        ToolObject klass = klassExpression.evaluate(memory);
        if (!klass.getBelongingClass().isOrExtends(memory.baseTypes().C_CLASS)) {
            throw new InvalidTypeException(memory, "'"+klassExpression+"' is not a valid class");
        }
        ToolClass.putExtensor((ToolClass) klass, extensor);
        memory.getTopScope().addOnPopAction(m -> ToolClass.removeExtensor((ToolClass) klass, extensor));
        return extensor;
    }
}
