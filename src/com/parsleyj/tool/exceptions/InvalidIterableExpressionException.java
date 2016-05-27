package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class InvalidIterableExpressionException extends ToolNativeException {
    public InvalidIterableExpressionException(Memory m, String msg){
        super(m.baseTypes().C_INVALID_ITERABLE_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
