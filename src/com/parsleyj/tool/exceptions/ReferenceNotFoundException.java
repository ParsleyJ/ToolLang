package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ReferenceNotFoundException extends ToolNativeException {
    public ReferenceNotFoundException(String msg) {
        super(BaseTypes.C_REFERENCE_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
