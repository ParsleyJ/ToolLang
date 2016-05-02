package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class CallOnNullException extends ToolNativeException {
    public CallOnNullException(String msg) {
        super(BaseTypes.C_CALL_ON_NULL_EXCEPTION.newExceptionInstance(msg));
    }
}
