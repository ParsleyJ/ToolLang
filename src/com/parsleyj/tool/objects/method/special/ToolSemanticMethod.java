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
    public static final String METHOD_CATEGORY_SEMANTIC = "METHOD_CATEGORY_SEMANTIC";

    protected ToolSemanticMethod(Visibility visibility, String name, ParameterDefinition[] parameters, RValue body) {
        super(METHOD_CATEGORY_SEMANTIC, visibility, name, parameters, body);
    }
}
