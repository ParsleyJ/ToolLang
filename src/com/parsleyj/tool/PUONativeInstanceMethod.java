package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class PUONativeInstanceMethod<T extends ToolObject> extends ToolMethod {

    @SuppressWarnings("unchecked") //there is a try/catch for that
    public PUONativeInstanceMethod(String name, ToolClass expressionType, Body<T> body) {
        super(
                Visibility.Public,
                name,
                new ParameterDefinition[]{}, (memory) -> {
                    ToolObject self = memory.getObjectByIdentifier("this");
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
        public T exec(T a, Memory memory) throws ToolInternalException;
    }
}
