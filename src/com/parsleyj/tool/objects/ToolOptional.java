package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.NullValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.types.TypeAdapter;

/**
 * Created by Giuseppe on 06/06/16.
 * TODO: javadoc
 */
public class ToolOptional extends ToolObject{
    private final ToolObject object;

    public ToolOptional(Memory m, ToolObject object){
        super(m, m.baseTypes().C_OPTIONAL);
        this.object = object;
    }

    public ToolOptional(Memory m){
        super(m, m.baseTypes().C_OPTIONAL);
        this.object = m.baseTypes().O_NULL;
    }

    public ToolObject getObject() {
        return object;
    }

    @NativeInstanceMethod(value = "isNull", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolBoolean isNull(@MemoryParameter Memory memory){
        return new ToolBoolean(memory, this.object.isNull());
    }

    @NativeInstanceMethod(value = "()", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public ToolObject roundBrackets(@MemoryParameter Memory memory) throws NullValueException {
        return value(memory, this);
    }

    @NativeInstanceMethod(value = "value", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolObject value(@MemoryParameter Memory memory,
                                           @SelfParameter ToolOptional self) throws NullValueException {
        if(self.object.isNull()){
            throw new NullValueException(memory, "");
        }
        return self.object;
    }

    @NativeClassMethod(value = "<>", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolObject parametrizeType(@MemoryParameter Memory memory,
                                             @SelfParameter ToolClass optionalSelfClass,
                                             ToolType typeArg) throws ToolNativeException{
        return new ParameterizedOptionalType(memory, optionalSelfClass, typeArg);

    }

    public static class ParameterizedOptionalType extends TypeAdapter{

        private final ToolClass optionalSelfClass;
        private final ToolType parameterType;

        public ParameterizedOptionalType(Memory m, ToolClass optionalSelfClass, ToolType parameterType){
            super(m, m.baseTypes().C_OBJECT);
            this.optionalSelfClass = optionalSelfClass;

            this.parameterType = parameterType;
        }

        public ToolType getParameterType() {
            return parameterType;
        }

        @Override
        public String getTypeName() throws ToolNativeException {
            return optionalSelfClass.getTypeName()+"<"+ parameterType.getTypeName()+">";
        }

        @Override
        public boolean isOperator(ToolObject o) throws ToolNativeException {
            return o.isNull() || parameterType.isOperator(o);
        }

        @Override
        public boolean canBeUsedAs(ToolType other) throws ToolNativeException {
            if(optionalSelfClass.canBeUsedAs(other))return true;
            if(other.canBeUsedAs(optionalSelfClass)) {
                if(other instanceof ParameterizedOptionalType){
                    ParameterizedOptionalType o = (ParameterizedOptionalType) other;
                    return this.getParameterType().canBeUsedAs(o.getParameterType());
                }
            }
            return false;
        }

        @Override
        public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
            if(from.isNull()) return 0;
            int total = memory.baseTypes().C_OPTIONAL.getObjectConvertibility(from);
            if(from instanceof ToolOptional){
                ToolType fromParametricType =  ((ToolOptional) from).getObject().getBelongingClass();
                total += parameterType.getConvertibility(fromParametricType);
            }
            return total;
        }

        @Override
        public int getConvertibility(ToolType from) throws ToolNativeException {
            int total = optionalSelfClass.getConvertibility(from);

            if(from instanceof ParameterizedOptionalType){
                ParameterizedOptionalType o = (ParameterizedOptionalType) from;
                total += this.getParameterType().getConvertibility(o.getParameterType());
            }

            return total;
        }
    }
}
