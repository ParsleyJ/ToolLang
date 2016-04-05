package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class SequentialComposition implements RValue {
    private final RValue c1;
    private final RValue c2;

    public SequentialComposition(RValue c1, RValue c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        c1.evaluate(memory);
        return c2.evaluate(memory);
    }

    @Override
    public String toString() {
        return c1 + "; " + c2;
    }
}
