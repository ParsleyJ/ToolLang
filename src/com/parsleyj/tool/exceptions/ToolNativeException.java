package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.ToolException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolNativeException extends Exception{
    private ToolException exceptionObject;

    public ToolNativeException(ToolException exceptionObject){
        this.exceptionObject = exceptionObject;
    }

    public ToolException getExceptionObject() {
        return exceptionObject;
    }
}
