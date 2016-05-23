package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class InvalidTypeException extends ToolNativeException {
    public InvalidTypeException(String msg){
        super(BaseTypes.C_INVALID_TYPE_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}
