package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class BadMethodCallException extends ToolNativeException {
    public BadMethodCallException(String msg) {
        super(BaseTypes.C_BAD_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
