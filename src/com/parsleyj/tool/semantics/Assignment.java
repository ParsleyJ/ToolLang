package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class Assignment implements RValue {
    private final LValue lVal;
    private final RValue exp;

    public Assignment(LValue lVal, RValue exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject o = exp.evaluate(memory);
        lVal.assign(o, memory);
        return o;
    }

    @Override
    public String toString() {
        return lVal + " = " + exp;
    }
}
