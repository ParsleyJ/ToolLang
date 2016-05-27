package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class InvalidConditionalExpressionException extends ToolNativeException {
    public InvalidConditionalExpressionException(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
