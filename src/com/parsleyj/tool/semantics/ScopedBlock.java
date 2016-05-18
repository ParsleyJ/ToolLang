package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ScopedBlock implements RValue {

    private RValue c;

    public ScopedBlock(RValue c) {
        this.c = c;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        return MethodCall.executeScopedBlockWithNoParameters(c, memory);
    }

    @Override
    public String toString() {
        return "{" + c + "}";
    }

    public RValue getUnevaluatedExpression() {
        return c;
    }
}
