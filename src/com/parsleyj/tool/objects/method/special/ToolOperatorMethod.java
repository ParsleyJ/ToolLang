package com.parsleyj.tool.objects.method.special;

import com.parsleyj.tool.objects.method.ParameterDefinition;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.RValue;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolOperatorMethod extends ToolMethod {

    public enum Mode{Prefix, Suffix, Binary, MultiEnclosedSuffix}

    public static final String METHOD_CATEGORY_OPERATOR = "METHOD_CATEGORY_OPERATOR";

    public ToolOperatorMethod(Visibility visibility, Mode mode, String operatorSym, ParameterDefinition[] parameterDefinitions, RValue body) {
        super(METHOD_CATEGORY_OPERATOR,
                visibility,
                "#OP# "+operatorSym+" #"+mode.name(),
                parameterDefinitions, body);
    }

    public ToolOperatorMethod(Visibility visibility, Mode mode, String operatorSym, ParameterDefinition[] parameterDefinitions, RValue condition, RValue body) {
        super(METHOD_CATEGORY_OPERATOR,
                visibility,
                "operator "+operatorSym+" "+mode.name(),
                parameterDefinitions,
                condition,
                body);
    }
}
