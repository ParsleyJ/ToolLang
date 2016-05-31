package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 30/05/16.
 * TODO: javadoc
 */
public class InvalidBreakExpression extends ToolNativeException {
    public InvalidBreakExpression(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_BREAK_EXPRESSION.newExceptionInstance(msg));
    }
}
