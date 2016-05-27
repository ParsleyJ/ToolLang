package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

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
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        if (RValue.evaluateAsConditional(condition, memory)) {
            return thenBranch.evaluate(memory);
        } else {
            return memory.baseTypes().O_NULL;
        }
    }

    @Override
    public String toString() {
        return "if " + condition + " then " + thenBranch;
    }
}
