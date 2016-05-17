package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolInterface;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.Identifier;
import com.parsleyj.tool.semantics.RValue;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

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

    private ToolClass parameterType;
    private Mode mode = Mode.ByReference;
    public FormalParameter(String parameterName, ToolClass parameterType) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    public FormalParameter(String parameterName, ToolClass parameterType, Mode mode) {
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        this.mode = mode;
    }

    public FormalParameter(String parameterName, ToolInterface interfaceType) {
        this.parameterName = parameterName;
        //TODO: impl
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
