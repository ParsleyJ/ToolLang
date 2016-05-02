package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;

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
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
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
