package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class InvalidParameterTypeException extends ToolNativeException {
    public InvalidParameterTypeException(String msg){
        super(BaseTypes.C_INVALID_PARAMETER_TYPE_EXCEPTION.newExceptionInstance(msg));
    }
}
