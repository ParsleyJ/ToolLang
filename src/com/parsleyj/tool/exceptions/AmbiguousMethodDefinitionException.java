package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 09/05/16.
 * TODO: javadoc
 */
public class AmbiguousMethodDefinitionException extends ToolNativeException {
    public AmbiguousMethodDefinitionException(String msg) {
        super(BaseTypes.C_AMBIGUOUS_METHOD_DEFINITION_EXCEPTION.newExceptionInstance(msg));
    }
}
