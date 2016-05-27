package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class InvalidTypeException extends ToolNativeException {
    public InvalidTypeException(Memory m, String msg){
        super(m.baseTypes().C_INVALID_TYPE_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
