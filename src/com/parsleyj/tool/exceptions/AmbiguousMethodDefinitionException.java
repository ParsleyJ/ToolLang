package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 09/05/16.
 * TODO: javadoc
 */
public class AmbiguousMethodDefinitionException extends ToolNativeException {
    public AmbiguousMethodDefinitionException(Memory m, String msg) {
        super(m.baseTypes().C_AMBIGUOUS_METHOD_DEFINITION_EXCEPTION.newExceptionInstance(msg));
    }
}
