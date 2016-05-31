package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

import static com.parsleyj.tool.semantics.flowcontrol.BreakStatement.BREAKABLE_SCOPE_TAG;
import static com.parsleyj.tool.semantics.flowcontrol.ForInStatement.LOOP_SCOPE_TAG;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class WhileStatement implements RValue {
    public static final String WHILE_LOOP_SCOPE_TAG = "while";
    private RValue condition;
    private RValue doBranch;

    public WhileStatement(RValue condition, RValue doBranch) {
        this.condition = condition;
        this.doBranch = doBranch;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject result = null;
        memory.pushScope();
        memory.getTopScope().addTag(BREAKABLE_SCOPE_TAG);
        memory.getTopScope().addTag(LOOP_SCOPE_TAG);
        memory.getTopScope().addTag(WHILE_LOOP_SCOPE_TAG);
        try {
            while (condition.evaluateAsConditional(memory)) {
                result = doBranch.evaluate(memory);
            }
        } catch (BreakStatement.Break e) {
            if (memory.getTopScope().containsTag(e.getTag())) {
                memory.popScope();
                return e.getResult();
            } else {
                throw e;
            }
        }
        memory.popScope();
        return result;
    }

    @Override
    public String toString() {
        return "while " + condition + " do " + doBranch;
    }
}
