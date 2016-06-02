package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 02/06/16.
 * TODO: javadoc
 */
public class BadReturnedValueException extends ToolNativeException {
    public BadReturnedValueException(Memory m, String msg) {
        super(m.baseTypes().C_BAD_RETURNED_VALUE_EXCEPTION.newExceptionInstance(msg));
    }
}
