package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolInterface;
import com.parsleyj.tool.objects.ToolType;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class FormalParameter {



    public enum Mode{
        ByValue,
        ByReference;
    }
    private String parameterName;

    private ToolType parameterType;
    private Mode mode = Mode.ByReference;
    public FormalParameter(String parameterName, ToolType parameterType) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    public FormalParameter(String parameterName, ToolType parameterType, Mode mode) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.mode = mode;
    }


    public String getParameterName() {
        return parameterName;
    }

    public ToolType getParameterType() {
        return parameterType;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "<*Parameter: "+(parameterType == null? "$nulltype$" : parameterType.getTypeName())+" "+parameterName+"*>";
    }
}
