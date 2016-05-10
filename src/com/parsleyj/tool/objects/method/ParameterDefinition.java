package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.objects.ToolClass;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ParameterDefinition {
    public enum Mode{
        ByValue,
        ByReference,
        ByName
    }

    private String parameterName;
    private ToolClass parameterType;
    private Mode mode = Mode.ByReference;

    public ParameterDefinition(String parameterName, ToolClass parameterType) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    public ParameterDefinition(String parameterName, ToolClass parameterType, Mode mode) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.mode = mode;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ToolClass getParameterType() {
        return parameterType;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "<*Parameter: "+(parameterType == null? "$nulltype$" : parameterType.getClassName())+" "+parameterName+"*>";
    }
}
