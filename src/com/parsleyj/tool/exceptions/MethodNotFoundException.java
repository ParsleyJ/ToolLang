package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class MethodNotFoundException extends ToolInternalException {
    public MethodNotFoundException(String msg) {
        super(BaseTypes.C_METHOD_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
