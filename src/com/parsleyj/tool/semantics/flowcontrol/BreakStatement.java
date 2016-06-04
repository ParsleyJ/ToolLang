package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.InvalidBreakExpression;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.*;

/**
 * Created by Giuseppe on 30/05/16.
 * TODO: javadoc
 */
public class BreakStatement implements RValue{

    public static final String BREAKABLE_SCOPE_TAG = "breakable";


    private final String tag;
    private final RValue resultExpression;

    public BreakStatement(RValue resultExpression) {
        this(BREAKABLE_SCOPE_TAG, resultExpression);
    }

    public BreakStatement(String tag, RValue resultExpression){
        this.tag = tag;
        this.resultExpression = resultExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject result = resultExpression.evaluate(memory);
        ArrayDeque<Memory.Scope> stack = memory.getCurrentFrameStack();
        while (!stack.isEmpty()&&stack.getLast().contains(tag)) {
            stack.removeLast();
        }
        if(stack.isEmpty()) throw new InvalidBreakExpression(memory, "break expression outside of breakable scope");
        throw new Break(tag, result);
    }

    public class Break extends RuntimeException {
        private String tag;
        private ToolObject result;

        public Break(String tag, ToolObject result) {
            super("break expression outside of breakable scope");
            this.tag = tag;
            this.result = result;
        }

        public String getTag() {
            return tag;
        }

        public ToolObject getResult() {
            return result;
        }

    }
}