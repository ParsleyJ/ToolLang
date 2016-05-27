package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class InvalidIndexTypeException extends ToolNativeException {
    public InvalidIndexTypeException(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_INDEX_TYPE_EXCEPTION.newExceptionInstance(msg));
    }

}
