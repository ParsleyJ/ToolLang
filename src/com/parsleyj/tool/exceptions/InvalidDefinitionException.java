package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 24/05/16.
 * TODO: javadoc
 */
public class InvalidDefinitionException extends ToolNativeException {
    public InvalidDefinitionException(Memory m, String msg) {
        super(m.baseTypes().C_INVALID_DEFINITION_EXCEPTION.newExceptionInstance(msg));
    }
}
