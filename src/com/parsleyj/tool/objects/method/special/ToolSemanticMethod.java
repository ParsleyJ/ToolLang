package com.parsleyj.tool.objects.method.special;

import com.parsleyj.tool.objects.method.ParameterDefinition;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.RValue;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolSemanticMethod extends ToolMethod {
    public static final String METHOD_CATEGORY_SPECIAL = "METHOD_CATEGORY_SPECIAL";

    public ToolSemanticMethod(Visibility visibility, String name, ParameterDefinition[] parameters, RValue body) {
        super(METHOD_CATEGORY_SPECIAL, visibility, name, parameters, body);
    }

    public ToolSemanticMethod(Visibility visibility, String name, ParameterDefinition[] parameters, RValue condition, RValue body) {
        super(METHOD_CATEGORY_SPECIAL, visibility, name, parameters, condition, body);
    }
}
