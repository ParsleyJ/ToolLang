package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class NameNotFoundException extends ToolNativeException {
    public NameNotFoundException(Memory m, String msg){
        super(m.baseTypes().C_NAME_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
