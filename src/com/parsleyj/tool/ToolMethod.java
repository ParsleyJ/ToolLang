package com.parsleyj.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolMethod extends ToolObject {
    private Visibility visibility;
    private String name;
    private List<ToolClass> argumentTypes = new ArrayList<>();
    private List<String> argumentNames = new ArrayList<>();
    private RValue body;

    public ToolMethod(Visibility visibility, String name, ParameterDefinition[] parameters, RValue body) {
        super(BaseTypes.C_METHOD);
        this.visibility = visibility;
        this.name = name;
        for (ParameterDefinition parameter : parameters) {
            this.argumentTypes.add(parameter.getParameterType());
            this.argumentNames.add(parameter.getParameterName());
        }
        this.body = body;
    }

    public RValue getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public List<ToolClass> getArgumentTypes() {
        return argumentTypes;
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}
