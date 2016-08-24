package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 30/05/16.
 * TODO: javadoc
 */
public class InvalidReturnExpression extends ToolNativeException {
    public InvalidReturnExpression(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_RETURN_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
