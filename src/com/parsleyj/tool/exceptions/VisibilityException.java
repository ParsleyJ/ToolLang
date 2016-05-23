package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class VisibilityException extends ToolNativeException {
    public VisibilityException(String msg) {
        super(BaseTypes.C_VISIBILITY_EXCEPTION.newExceptionInstance(msg));
    }
}
