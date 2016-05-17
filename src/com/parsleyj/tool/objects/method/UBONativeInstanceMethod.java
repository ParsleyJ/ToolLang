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
public class UBONativeInstanceMethod<R extends ToolObject, T1 extends ToolObject, T2 extends ToolObject> extends ToolMethod {

    @SuppressWarnings("unchecked") //there is a try/catch for that
    public UBONativeInstanceMethod(String methodCategory, String name, ToolClass expressionType1, ToolClass expressionType2, Body<R, T1, T2> body) {
        super(methodCategory,
                Visibility.Public,
                name,
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, expressionType1),
                        new FormalParameter(Memory.ARG_IDENTIFIER, expressionType2)
                },
                new FormalParameter[]{},
                (memory) -> {
                    ToolObject self = memory.getObjectByIdentifier(Memory.SELF_IDENTIFIER);
                    ToolObject x = memory.getObjectByIdentifier(Memory.ARG_IDENTIFIER);
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
        public R exec(T1 a, T2 b, Memory memory) throws ToolNativeException;
    }
}
