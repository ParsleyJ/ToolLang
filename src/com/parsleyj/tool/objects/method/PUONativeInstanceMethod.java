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
public class PUONativeInstanceMethod<T extends ToolObject> extends ToolMethod {

    @SuppressWarnings("unchecked") //there is a try/catch for that
    public PUONativeInstanceMethod(String name, ToolClass expressionType, Body<T> body) {
        super(Visibility.Public,
                name,
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, expressionType)
                },
                new FormalParameter[]{}, (memory) -> {
                    ToolObject self = memory.getObjectByIdentifier(Memory.SELF_IDENTIFIER);
                    ToolObject result = null;
                    try {
                        if (self.getBelongingClass().isOrExtends(expressionType)) {
                            result = body.exec((T) self, memory);
                        } else throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType+">."+name+"()");
                    } catch (ClassCastException cce) {
                        throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType+">."+name+"()");
                    }
                    return result;
                });
    }

    public interface Body<T extends ToolObject> {
        public T exec(T a, Memory memory) throws ToolNativeException;
    }
}
