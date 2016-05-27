package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 20/05/16.
 * TODO: javadoc
 */
public class NameAlreadyUsedException extends ToolNativeException {
    public NameAlreadyUsedException(Memory m, String msg) {
        super(m.baseTypes().C_NAME_ALREADY_USED_EXCEPTION.newExceptionInstance(msg));
    }
}
