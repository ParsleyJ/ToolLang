package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class BadMethodCallException extends ToolNativeException {
    public BadMethodCallException(Memory m, String msg) {
        super(m.baseTypes().C_BAD_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
