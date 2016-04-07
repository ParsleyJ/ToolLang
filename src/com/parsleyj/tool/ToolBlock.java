package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

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
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        if (evaluateAsObject) return super.evaluate(memory);
        else {
            evaluateAsObject = false;
            return execute(memory);
        }
    }

    public ToolObject execute(Memory memory) throws ToolInternalException {
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
