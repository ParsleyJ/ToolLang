package com.parsleyj.tool.objects.method.special;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.RValue;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolSetterMethod extends ToolMethod {

    public static final String METHOD_CATEGORY_SETTER = "METHOD_CATEGORY_SETTER";

    public ToolSetterMethod(String name, ToolClass selfType, ToolClass argType, RValue body) {
        super(METHOD_CATEGORY_SETTER, Visibility.Public, name, new FormalParameter[]{
                new FormalParameter(Memory.SELF_IDENTIFIER, selfType),
                new FormalParameter(Memory.ARG_IDENTIFIER, argType)
        }, new FormalParameter[]{}, body);
    }
}
