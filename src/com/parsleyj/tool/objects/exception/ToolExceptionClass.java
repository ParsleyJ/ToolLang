package com.parsleyj.tool.objects.exception;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolClass;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class ToolExceptionClass extends ToolClass {
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
