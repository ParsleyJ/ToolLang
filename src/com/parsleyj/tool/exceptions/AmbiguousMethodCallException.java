package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;
import com.parsleyj.tool.ToolException;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class AmbiguousMethodCallException extends ToolInternalException {
    public AmbiguousMethodCallException(String msg) {
        super(BaseTypes.C_AMBIGUOUS_METHOD_CALL_EXCEPTION.newExceptionInstance(msg));
    }
}
