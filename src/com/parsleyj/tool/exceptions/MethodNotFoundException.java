package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolType;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class MethodNotFoundException extends ToolNativeException {
    public MethodNotFoundException(Memory m, String msg) {
        super(m.baseTypes().C_METHOD_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }

    @NotNull
    public static String getDefaultMessage(String category, ToolObject owner, String name, List<ToolObject> arguments) {
        StringBuilder sb = new StringBuilder("Method not found: "+category+" "+
                (owner != null ? (owner + ".") : "") + name);

        if (!category.equals(ToolGetterMethod.METHOD_CATEGORY_GETTER)
                && !category.equals(ToolSetterMethod.METHOD_CATEGORY_SETTER)
                && !category.equals(ToolOperatorMethod.METHOD_CATEGORY_OPERATOR)) {
            sb.append("(");
            for (int i = 0; i < arguments.size(); i++) {
                ToolObject argument = arguments.get(i);
                sb.append(argument);
                if (i < arguments.size() - 1) sb.append(", ");
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
