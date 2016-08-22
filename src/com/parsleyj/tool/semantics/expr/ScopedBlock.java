package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.flowcontrol.BreakStatement;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ScopedBlock implements RValue {

    private RValue block;

    public ScopedBlock(RValue block) {
        this.block = block;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        memory.pushScope();
        ToolObject result;
        try {
            result = block.evaluate(memory);
        } catch (BreakStatement.Break e) {
            memory.popScope();
            throw e;
        }
        memory.popScope();
        return result;
    }

    @Override
    public String toString() {
        return "{" + block + "}";
    }

    public RValue getUnevaluatedExpression() {
        return block;
    }
}
