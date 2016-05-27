package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

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
        return null;
    }
}
