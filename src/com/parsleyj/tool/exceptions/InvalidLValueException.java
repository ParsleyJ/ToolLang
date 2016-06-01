package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.exception.ToolException;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class InvalidLValueException extends ToolNativeException {
    public InvalidLValueException(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_L_VALUE_EXCEPTION.newExceptionInstance(msg));
    }
}
