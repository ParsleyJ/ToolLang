package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ReferenceNotFoundException extends ToolNativeException {
    public ReferenceNotFoundException(Memory m, String msg) {
        super(m.baseTypes().C_REFERENCE_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
