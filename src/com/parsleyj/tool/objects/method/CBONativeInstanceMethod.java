package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class CBONativeInstanceMethod<T extends ToolObject> extends ToolMethod {

    @SuppressWarnings("unchecked") //there is a try/catch for that
    public CBONativeInstanceMethod(String name, ToolClass expressionType, String parameterName, Body<T> body) {
        super(Visibility.Public,
                name,
                new ParameterDefinition[]{
                        new ParameterDefinition(Memory.SELF_IDENTIFIER, expressionType)
                },
                new ParameterDefinition[]{
                        new ParameterDefinition(parameterName, expressionType)
                }, (memory) -> {
                    ToolObject self = memory.getObjectByIdentifier(Memory.SELF_IDENTIFIER);
                    ToolObject x = memory.getObjectByIdentifier(parameterName);
                    ToolObject result = null;
                    try {
                        if (self.getBelongingClass().isOrExtends(expressionType) && x.getBelongingClass().isOrExtends(expressionType)) {
                            result = body.exec((T) self, (T) x, memory);
                        } else throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType+">."+name+"("+expressionType+")");
                    } catch (ClassCastException cce) {
                        throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType+">."+name+"("+expressionType+")");
                    }
                    return result;
                });
    }

    public interface Body<T extends ToolObject> {
        public T exec(T a, T b, Memory memory) throws ToolNativeException;
    }
}
