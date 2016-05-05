package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class InvalidIndexTypeException extends ToolNativeException {
    public InvalidIndexTypeException(String msg) {
        super(BaseTypes.C_INVALID_INDEX_TYPE_EXCEPTION.newExceptionInstance(msg));
    }

}
