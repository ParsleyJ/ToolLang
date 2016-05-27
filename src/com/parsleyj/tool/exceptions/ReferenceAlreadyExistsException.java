package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ReferenceAlreadyExistsException extends ToolNativeException {
    public ReferenceAlreadyExistsException(Memory m, String msg) {
        super(m.baseTypes().C_REFERENCE_ALREADY_EXISTS_EXCEPTION.newExceptionInstance(msg));
    }
}
