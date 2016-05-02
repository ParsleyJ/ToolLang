package com.parsleyj.tool.objects;

import com.parsleyj.tool.semantics.RValue;

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

    public String completeInstanceMethodName(ToolClass self){
        StringBuilder sb = new StringBuilder("<"+self.getClassName()+">."+name+"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    public String completeClassMethodName(ToolClass self){
        StringBuilder sb = new StringBuilder(self.getClassName()+"."+name+"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    private void addParameterListToStringBuilder(StringBuilder sb){
        for (int i = 0; i < argumentTypes.size(); i++) {
            ToolClass argumentType = argumentTypes.get(i);
            sb.append("<").append(argumentType.getClassName()).append(">");
            if(i < argumentTypes.size()-1) sb.append(", ");
        }
    }
}
