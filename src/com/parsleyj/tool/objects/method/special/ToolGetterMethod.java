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
public class ToolGetterMethod extends ToolMethod {

    public static final String METHOD_CATEGORY_GETTER = "METHOD_CATEGORY_GETTER";

    public ToolGetterMethod(String name, ToolClass selfType, RValue body) {
        super(
                METHOD_CATEGORY_GETTER,
                Visibility.Public,
                name,
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, selfType)
                },
                new FormalParameter[]{},
                body);
    }
}
