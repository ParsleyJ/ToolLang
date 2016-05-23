package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ExpressionBlock implements RValue {

    private RValue c;

    public ExpressionBlock(RValue c) {
        this.c = c;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        return c.evaluate(memory);
    }

    @Override
    public String toString() {
        return "(" + c + ")";
    }

    public RValue getUnevaluatedExpression() {
        return c;
    }
}
