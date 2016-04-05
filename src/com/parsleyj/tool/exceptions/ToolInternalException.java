package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.ToolException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolInternalException extends Exception{
    private ToolException exceptionObject;

    public ToolInternalException(ToolException exceptionObject){
        this.exceptionObject = exceptionObject;
    }

    public ToolException getExceptionObject() {
        return exceptionObject;
    }
}
