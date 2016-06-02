package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.BadReturnedValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.nametabled.DefinitionGetter;
import com.parsleyj.tool.semantics.nametabled.DefinitionMethod;
import com.parsleyj.tool.semantics.nametabled.DefinitionOperator;
import com.parsleyj.tool.semantics.util.MethodCall;

import java.util.Collections;


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

    @FunctionalInterface
    public interface IsOperatorImplementation{
        boolean isOperator(Memory m, ToolObject self, ToolObject o);
    }
    @FunctionalInterface
    public interface CanBeUsedAsImplementation{
        boolean canBeUsedAs(Memory m, ToolObject self, ToolType other);
    }
    @FunctionalInterface
    public interface GetObjectConvertibilityImplementation{
        int getObjectConvertibility(Memory m, ToolObject self, ToolObject from);
    }
    @FunctionalInterface
    public interface GetConvertibilityImplementation{
        int getConvertibility(Memory m, ToolObject self, ToolType from);
    }

    public static ToolObject createTypeObject(Memory memory,
                                              String typename,
                                              IsOperatorImplementation isOperatorImplementation,
                                              CanBeUsedAsImplementation canBeUsedAsImplementation,
                                              GetObjectConvertibilityImplementation getObjectConvertibilityImplementation,
                                              GetConvertibilityImplementation getConvertibilityImplementation)
                                        throws ToolNativeException {

        ToolObject result = new ToolObject(memory);
        result.addMethod(DefinitionGetter.createGetter(memory, "name", (m) -> new ToolString(m, typename)));
        result.addMethod(DefinitionOperator.createBinaryOperator(memory,
                ToolOperatorMethod.Mode.Binary, "is",
                new FormalParameter(Memory.ARG_IDENTIFIER, memory.baseTypes().C_OBJECT),
                memory1 -> new ToolBoolean(memory1,
                        isOperatorImplementation.isOperator(memory1, result,
                                memory1.getObjectByIdentifier(Memory.ARG_IDENTIFIER)))
        ));
        result.addMethod(DefinitionMethod.createMethod(memory, "canBeUsedAs",
                Collections.singletonList(new FormalParameter("other", memory.baseTypes().I_TYPE)),
                memory1 -> {
                    ToolObject other = memory1.getObjectByIdentifier("other");
                    return new ToolBoolean(memory1,
                            canBeUsedAsImplementation.canBeUsedAs(memory1, result, new ToolTypeAdapter(memory1, other)));
                }
        ));
        result.addMethod(DefinitionMethod.createMethod(memory, "getObjectConvertibility",
                Collections.singletonList(new FormalParameter("from", memory.baseTypes().C_OBJECT)),
                memory1 -> {
                    ToolObject from = memory1.getObjectByIdentifier("from");
                    return new ToolInteger(memory1,
                            getObjectConvertibilityImplementation.getObjectConvertibility(
                                    memory1, result, from));
                }
        ));
        result.addMethod(DefinitionMethod.createMethod(memory, "getConvertibility",
                Collections.singletonList(new FormalParameter("from", memory.baseTypes().I_TYPE)),
                memory1 -> {
                    ToolObject from = memory1.getObjectByIdentifier("from");
                    return new ToolInteger(memory1,
                            getConvertibilityImplementation.getConvertibility(
                                    memory1, result, new ToolTypeAdapter(memory1, from)));
                }
        ));
        return result;
    }
}
