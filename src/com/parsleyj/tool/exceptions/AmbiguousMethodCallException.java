package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class AmbiguousMethodCallException extends ToolNativeException {
    public AmbiguousMethodCallException(String msg) {
        super(BaseTypes.C_AMBIGUOUS_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
