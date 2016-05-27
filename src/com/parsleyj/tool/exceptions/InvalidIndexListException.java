package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class InvalidIndexListException extends ToolNativeException {

    public InvalidIndexListException(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_INDEX_LIST_EXCEPTION.newExceptionInstance(msg));
    }
}
