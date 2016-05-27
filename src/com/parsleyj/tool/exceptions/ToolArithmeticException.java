package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class ToolArithmeticException extends ToolNativeException {
    public ToolArithmeticException(Memory m, String msg) {
        super(m.baseTypes().C_ARITHMETIC_EXCEPTION.newExceptionInstance(msg));
    }
}
