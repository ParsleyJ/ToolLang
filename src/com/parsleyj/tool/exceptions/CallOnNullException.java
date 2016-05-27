package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class CallOnNullException extends ToolNativeException {
    public CallOnNullException(Memory m, String msg) {
        super(m.baseTypes().C_CALL_ON_NULL_EXCEPTION.newExceptionInstance(msg));
    }
}
