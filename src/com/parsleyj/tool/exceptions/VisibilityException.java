package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class VisibilityException extends ToolNativeException {
    public VisibilityException(Memory m, String msg) {
        super(m.baseTypes().C_VISIBILITY_EXCEPTION.newExceptionInstance(msg));
    }
}
