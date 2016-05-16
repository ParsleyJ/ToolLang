package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.RValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */

public class ToolMethod extends ToolObject{

    public static final String METHOD_CATEGORY_METHOD = "METHOD_CATEGORY_METHOD";

    private String methodCategory;
    private Visibility visibility;
    private String name;
    private List<ToolClass> implicitArgumentTypes = new ArrayList<>();
    private List<String> implicitArgumentNames = new ArrayList<>();
    private List<ToolClass> argumentTypes = new ArrayList<>();
    private List<String> argumentNames = new ArrayList<>();
    private RValue condition;
    private RValue body;

    public ToolMethod(Visibility visibility, String name, ParameterDefinition[] implicitParameters, ParameterDefinition[] parameters, RValue body) {
        super(BaseTypes.C_METHOD);
        this.methodCategory = METHOD_CATEGORY_METHOD;
        this.visibility = visibility;
        this.name = name;
        for (ParameterDefinition parameter : parameters) {
            this.argumentTypes.add(parameter.getParameterType());
            this.argumentNames.add(parameter.getParameterName());
        }

        for (ParameterDefinition implicitParameter : implicitParameters) {
            this.implicitArgumentTypes.add(implicitParameter.getParameterType());
            this.implicitArgumentNames.add(implicitParameter.getParameterName());
        }
        this.condition = new ToolBoolean(true);
        this.body = body;
    }

    public ToolMethod(Visibility visibility, String name, ParameterDefinition[] implicitParameters, ParameterDefinition[] parameters, RValue condition, RValue body) {
        super(BaseTypes.C_METHOD);
        this.methodCategory = METHOD_CATEGORY_METHOD;
        this.visibility = visibility;
        this.name = name;
        for (ParameterDefinition parameter : parameters) {
            this.argumentTypes.add(parameter.getParameterType());
            this.argumentNames.add(parameter.getParameterName());

        }
        for (ParameterDefinition implicitParameter : implicitParameters) {
            this.implicitArgumentTypes.add(implicitParameter.getParameterType());
            this.implicitArgumentNames.add(implicitParameter.getParameterName());
        }
        this.condition = condition;
        this.body = body;
    }

    public ToolMethod(String methodCategory, Visibility visibility, String name, ParameterDefinition[] implicitParameters, ParameterDefinition[] parameters, RValue body){
        this(visibility, name, implicitParameters, parameters, body);
        this.methodCategory = methodCategory;
    }

    public ToolMethod(String methodCategory, Visibility visibility, String name, ParameterDefinition[] implicitParameters, ParameterDefinition[] parameters, RValue condition, RValue body){
        this(visibility, name, implicitParameters, parameters, condition, body);
        this.methodCategory = methodCategory;
    }

    public RValue getCondition() {
        return condition;
    }


    public String getMethodCategory() {
        return methodCategory;
    }

    public RValue getBody() {
        return body;
    }


    public String getMethodName() {
        return name;
    }


    public List<ToolClass> getArgumentTypes() {
        return argumentTypes;
    }


    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public List<ToolClass> getImplicitArgumentTypes() {
        return implicitArgumentTypes;
    }

    public List<String> getImplicitArgumentNames() {
        return implicitArgumentNames;
    }


    public Visibility getVisibility() {
        return visibility;
    }

    public String completeInstanceMethodName(ToolClass self){//TODO: change for categories
        StringBuilder sb = new StringBuilder("<"+self.getClassName()+">."+name+"(");
        addParameterListToStringBuilder(sb);
        sb.append(")");
        return sb.toString();
    }

    public String completeClassMethodName(ToolClass self){//TODO: change for categories
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
