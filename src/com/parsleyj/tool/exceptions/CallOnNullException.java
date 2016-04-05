package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class CallOnNullException extends ToolInternalException {
    public CallOnNullException(String msg) {
        super(BaseTypes.C_CALL_ON_NULL_EXCEPTION.newExceptionInstance(msg));
    }
}
