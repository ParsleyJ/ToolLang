package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 24/05/16.
 * TODO: javadoc
 */
public class InvalidDefinitionException extends ToolNativeException {
    public InvalidDefinitionException(String msg) {
        super(BaseTypes.C_INVALID_DEFINITION_EXCEPTION.newExceptionInstance(msg));
    }
}
