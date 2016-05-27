package com.parsleyj.tool.objects.exception;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class ToolExceptionClass extends ToolClass {
    private Memory memory;
    public ToolExceptionClass(Memory m, String className) {
        super(m, className, m.baseTypes().C_EXCEPTION);
        memory = m;
    }


    public ToolException newExceptionInstance(String message){
        return new ToolException(memory, this, message);
    }
}
