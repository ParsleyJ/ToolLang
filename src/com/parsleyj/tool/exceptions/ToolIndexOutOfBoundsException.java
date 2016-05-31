package com.parsleyj.tool.exceptions;


import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class ToolIndexOutOfBoundsException extends ToolNativeException {
    public ToolIndexOutOfBoundsException(Memory m, int index, int size) {
        super(m.baseTypes().C_INDEX_OUT_OF_BOUNDS_EXCEPTION.newExceptionInstance("index: "+index+"; size: "+size));
    }
}
