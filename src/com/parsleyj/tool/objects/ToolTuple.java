package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.types.TypeAdapter;

import java.util.List;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class ToolTuple extends ToolObject {
    private final List<ToolObject> objects;

    public ToolTuple(Memory m, List<ToolObject> objects) {
        super(m, m.baseTypes().C_TUPLE);
        this.objects = objects;
    }

    public List<ToolObject> getTupleObjects() {
        return objects;
    }

    @NativeInstanceMethod(value = ":=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Binary)
    public ToolObject destGetter(@MemoryParameter Memory memory, ToolInteger arg){
        return this.objects.get(arg.getIntegerValue());
    }

    @NativeClassMethod(value = "<>", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolObject parametrizeType(@MemoryParameter Memory memory,
                                             @SelfParameter ToolClass selfTupleClass,
                                             ToolType subType){
        return new ParametrizedTupleType(memory, subType);
    }

    public static class ParametrizedTupleType extends TypeAdapter {
        private final ToolType parameterType;
        private final ToolClass tupleClass;

        public ParametrizedTupleType(Memory m, ToolType parameterType) {
            super(m, m.baseTypes().C_OBJECT);
            this.tupleClass = m.baseTypes().C_TUPLE;
            this.parameterType = parameterType;
        }

        public ToolType getParameterType() {
            return parameterType;
        }

        @Override
        public String getTypeName() throws ToolNativeException {
            return tupleClass.getTypeName() + "<" + parameterType.getTypeName() + ">";
        }

        @Override
        public boolean isOperator(ToolObject o) throws ToolNativeException {
            if(!memory.baseTypes().C_TUPLE.isOperator(o)) return false;
            ToolTuple tuple = (ToolTuple) o;
            for (ToolObject object : tuple.getTupleObjects()) {
                if(!parameterType.isOperator(object)) return false;
            }
            return true;
        }

        @Override
        public boolean canBeUsedAs(ToolType other) throws ToolNativeException {
            if(tupleClass.canBeUsedAs(other)) return true;
            if(other.canBeUsedAs(tupleClass)) {
                if(other instanceof ParametrizedTupleType){
                    ParametrizedTupleType o = (ParametrizedTupleType) other;
                    return this.getParameterType().canBeUsedAs(o.getParameterType());
                }
            }
            return false;
        }

        @Override
        public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
            int total = memory.baseTypes().C_TUPLE.getObjectConvertibility(from);
            if(from instanceof ToolTuple){
                ToolType fromParametricType = memory.baseTypes().nativeAlgorithms.getSupEnclosingClass(memory, ((ToolTuple) from).getTupleObjects());
                total += parameterType.getConvertibility(fromParametricType);
            }
            return total;
        }

        @Override
        public int getConvertibility(ToolType from) throws ToolNativeException {
            int total = tupleClass.getConvertibility(from);

            if(from instanceof ParametrizedTupleType){
                ParametrizedTupleType o = (ParametrizedTupleType) from;
                total += this.getParameterType().getConvertibility(o.getParameterType());
            }

            return total;
        }
    }
}
