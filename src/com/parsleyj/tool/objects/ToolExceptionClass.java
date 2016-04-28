package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class ToolExceptionClass extends ToolClass{
    public ToolExceptionClass(String className) {
        super(className, BaseTypes.C_EXCEPTION);
    }

    public ToolInternalException newThrowableInstance(String message){
        return new ToolInternalException(new ToolException(this, message));
    }

    public ToolException newExceptionInstance(String message){
        return new ToolException(this, message);
    }
}
