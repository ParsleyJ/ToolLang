package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class BadMethodCallException extends ToolInternalException {
    public BadMethodCallException(String msg) {
        super(BaseTypes.C_BAD_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
