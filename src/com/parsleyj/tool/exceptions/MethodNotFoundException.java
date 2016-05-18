package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class MethodNotFoundException extends ToolNativeException {
    public MethodNotFoundException(String msg) {
        super(BaseTypes.C_METHOD_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }

    @NotNull //TODO: add category in message
    public static String getDefaultMessage(ToolObject caller, String name, List<ToolClass> argumentsTypes) {
        StringBuilder sb = new StringBuilder("Method not found: " + (caller != null ? (caller + ".") : "") + name + "(");
        for (int i = 0; i < argumentsTypes.size(); i++) {
            ToolClass argumentType = argumentsTypes.get(i);
            sb.append(argumentType.getClassName());
            if (i < argumentsTypes.size() - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
