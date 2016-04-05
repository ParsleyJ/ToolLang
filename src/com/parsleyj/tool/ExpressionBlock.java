package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

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
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        return ToolMethodCall.executeScopedBlockWithNoParameters(c, memory);
    }

    @Override
    public String toString() {
        return "(" + c + ")";
    }
}
