package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.BadReturnedValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.semantics.util.MethodCall;


/**
 * Created by Giuseppe on 02/06/16.
 * TODO: javadoc
 */
public class ToolTypeAdapter implements ToolType {


    private Memory memory;
    private final ToolObject toolTypeObject;

    public ToolTypeAdapter(Memory memory, ToolObject toolTypeObject) {
        this.memory = memory;
        this.toolTypeObject = toolTypeObject;
    }

    public boolean checkIsImplementingType(ToolInterface toolInterface) throws ToolNativeException {
        return toolTypeObject.respondsToInterface(toolInterface);
    }

    @Override
    public String getTypeName() throws ToolNativeException {
        ToolObject o = MethodCall.getter(toolTypeObject, "name").evaluate(memory);
        if(!memory.baseTypes().C_STRING.isOperator(o)) throw new BadReturnedValueException(memory,
                "Expected a String value");
        return ((ToolString) o).getStringValue();
    }

    @Override
    public boolean isOperator(ToolObject o) throws ToolNativeException {
        ToolObject x = MethodCall.binaryOperator(toolTypeObject, "is", o).evaluate(memory);
        if(!memory.baseTypes().C_BOOLEAN.isOperator(x)) throw new BadReturnedValueException(memory,
                "Expected a Boolean value");
        return ((ToolBoolean) x).getBoolValue();
    }

    @Override
    public boolean canBeUsedAs(ToolType other) throws ToolNativeException {
        if(!(other instanceof ToolObject)) throw new BadMethodCallException(memory,
                "Attempted to force call a method with bad argument at native level.");

        ToolObject x = MethodCall.method(toolTypeObject, "canBeUsedAs", new ToolObject[]{(ToolObject) other})
                .evaluate(memory);

        if(!memory.baseTypes().C_BOOLEAN.isOperator(x)) throw new BadReturnedValueException(memory,
                "Expected a Boolean value");
        return ((ToolBoolean) x).getBoolValue();
    }

    @Override
    public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
        ToolObject x = MethodCall.method(toolTypeObject, "getObjectConvertibility", new ToolObject[]{from})
                .evaluate(memory);

        if(!memory.baseTypes().C_INTEGER.isOperator(x)) throw new BadReturnedValueException(memory,
                "Expected a Integer value");
        return ((ToolInteger) x).getIntegerValue();
    }

    @Override
    public int getConvertibility(ToolType from) throws ToolNativeException {
        if(!(from instanceof ToolObject)) throw new BadMethodCallException(memory,
                "Attempted to force call a method with bad argument at native level.");

        ToolObject x = MethodCall.method(toolTypeObject, "getConvertibility", new ToolObject[]{(ToolObject)from})
                .evaluate(memory);

        if(!memory.baseTypes().C_INTEGER.isOperator(x)) throw new BadReturnedValueException(memory,
                "Expected a Integer value");
        return ((ToolInteger) x).getIntegerValue();
    }
}
