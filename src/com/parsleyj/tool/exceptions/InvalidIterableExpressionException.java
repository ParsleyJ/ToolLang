package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class InvalidIterableExpressionException extends ToolNativeException {
    public InvalidIterableExpressionException(String msg){
        super(BaseTypes.C_INVALID_ITERABLE_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
