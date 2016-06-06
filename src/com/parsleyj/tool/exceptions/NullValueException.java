package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.exception.ToolException;

/**
 * Created by Giuseppe on 06/06/16.
 * TODO: javadoc
 */
public class NullValueException extends ToolNativeException {
    public NullValueException(Memory m, String msg) {
        super(m.baseTypes().C_NULL_VALUE_EXCEPTION.newExceptionInstance(msg));
    }
}
