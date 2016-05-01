package com.parsleyj.tool.objects;

import com.parsleyj.tool.ParameterDefinition;
import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.ToolInternalException;
import com.parsleyj.tool.memory.Memory;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class UBONativeInstanceMethod<R extends ToolObject, T1 extends ToolObject, T2 extends ToolObject> extends ToolMethod {

    @SuppressWarnings("unchecked") //there is a try/catch for that
    public UBONativeInstanceMethod(String name, ToolClass expressionType1, ToolClass expressionType2, String parameterName, Body<R, T1, T2> body) {
        super(
                Visibility.Public,
                name,
                new ParameterDefinition[]{
                        new ParameterDefinition(parameterName, expressionType2)
                }, (memory) -> {
                    ToolObject self = memory.getObjectByIdentifier("this");
                    ToolObject x = memory.getObjectByIdentifier("x");
                    ToolObject result = null;
                    try {
                        if (self.getBelongingClass().isOrExtends(expressionType1) && x.getBelongingClass().isOrExtends(expressionType2)) {
                            result = body.exec((T1) self, (T2) x, memory);
                        } else throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType1+">."+name+"("+expressionType2+")");
                    } catch (ClassCastException cce) {
                        throw new BadMethodCallException(
                                "Something went wrong while attempting to call <"+expressionType1+">."+name+"("+expressionType2+")");
                    }
                    return result;
                });
    }

    public interface Body<R extends ToolObject, T1 extends ToolObject, T2 extends ToolObject> {
        public R exec(T1 a, T2 b, Memory memory) throws ToolInternalException;
    }
}