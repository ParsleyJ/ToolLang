package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class ToolExceptionClass extends ToolClass{
    public ToolExceptionClass(String className) {
        super(className, BaseTypes.C_EXCEPTION);
    }

    public ToolNativeException newThrowableInstance(String message){
        return new ToolNativeException(new ToolException(this, message));
    }

    public ToolException newExceptionInstance(String message){
        return new ToolException(this, message);
    }
}
