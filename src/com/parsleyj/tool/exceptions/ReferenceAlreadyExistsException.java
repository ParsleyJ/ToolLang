package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ReferenceAlreadyExistsException extends ToolInternalException {
    public ReferenceAlreadyExistsException(String msg) {
        super(BaseTypes.C_REFERENCE_ALREADY_EXISTS_EXCEPTION.newExceptionInstance(msg));
    }
}