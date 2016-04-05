package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class IfThenStatement implements RValue {
    private RValue condition;
    private RValue thenBranch;

    public IfThenStatement(RValue condition, RValue thenBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        if (RValue.evaluateAsConditional(condition, memory)) {
            return thenBranch.evaluate(memory);
        } else {
            return BaseTypes.O_NULL;
        }
    }

    @Override
    public String toString() {
        return "if " + condition + " then " + thenBranch;
    }
}
