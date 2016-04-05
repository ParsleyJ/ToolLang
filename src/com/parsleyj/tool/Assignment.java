package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class Assignment implements RValue {
    private final LValue lval;
    private final RValue exp;

    public Assignment(LValue lval, RValue exp) {
        this.lval = lval;
        this.exp = exp;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        ToolObject o = exp.evaluate(memory);
        lval.assign(o, memory);
        return o;
    }

    @Override
    public String toString() {
        return lval + " = " + exp;
    }
}
