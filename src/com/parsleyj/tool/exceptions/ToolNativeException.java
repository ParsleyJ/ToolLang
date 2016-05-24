package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.exception.ToolException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolNativeException extends Exception{
    private ToolException exceptionObject;
    private List<String> frameTrace = new ArrayList<>();

    public ToolNativeException(ToolException exceptionObject){
        this.exceptionObject = exceptionObject;
    }

    public ToolException getExceptionObject() {
        return exceptionObject;
    }

    public void addFrameToTrace(String frame){
        frameTrace.add(frame);
    }

    public String getFrameTrace(){
        String ft = "";
        for (String s : frameTrace) {
            ft += s + "\n";
        }
        return ft;
    }
}
