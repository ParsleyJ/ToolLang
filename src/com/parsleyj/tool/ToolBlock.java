package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolBlock extends ToolObject {
    private RValue x;
    private boolean evaluateAsObject = true;

    public ToolBlock(RValue x) {
        super(BaseTypes.C_BLOCK);
        this.x = x;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        if (evaluateAsObject) return super.evaluate(memory);
        else {
            evaluateAsObject = false;
            return execute(memory);
        }
    }

    public ToolObject execute(Memory memory) throws ToolNativeException {
        return MethodCall.executeScopedBlockWithNoParameters(x, memory);
    }

    @Override
    public String toString() {
        return "{" + x + "}";
    }

    public void setEvaluateAsObject(boolean evaluateAsObject) {
        this.evaluateAsObject = evaluateAsObject;
    }
}
