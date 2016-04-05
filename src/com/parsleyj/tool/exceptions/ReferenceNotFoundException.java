package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ReferenceNotFoundException extends ToolInternalException {
    public ReferenceNotFoundException(String msg) {
        super(BaseTypes.C_REFERENCE_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
