package com.parsleyj.tool.objects.method.special;

import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolCtorMethod extends ToolMethod{
    public static final String METHOD_CATEGORY_CONSTRUCTOR = "METHOD_CATEGORY_CONSTRUCTOR";

    protected ToolCtorMethod(Visibility visibility, String name, FormalParameter[] implicitParameters, FormalParameter[] parameters, RValue condition, RValue body) {
        super(METHOD_CATEGORY_CONSTRUCTOR, visibility, name, implicitParameters, parameters, condition, body);
    }
}
