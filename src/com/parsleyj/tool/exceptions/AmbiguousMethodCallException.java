package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class AmbiguousMethodCallException extends ToolNativeException {
    public AmbiguousMethodCallException(Memory m, String msg) {
        super(m.baseTypes().C_AMBIGUOUS_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
