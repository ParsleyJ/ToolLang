package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class WhileStatement implements RValue {
    private RValue condition;
    private RValue doBranch;

    public WhileStatement(RValue condition, RValue doBranch) {
        this.condition = condition;
        this.doBranch = doBranch;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        ToolObject result = null;
        while (RValue.evaluateAsConditional(condition, memory)) {
            result = doBranch.evaluate(memory);
        }
        return result;
    }

    @Override
    public String toString() {
        return "while " + condition + " do " + doBranch;
    }
}
