package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.types.TypeAdapter;
import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.utils.Pair;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

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

    public int size(){
        return getTupleObjects().size();
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
                                             ToolType subType) throws ToolNativeException{
        ParametrizedTupleType result = new ParametrizedTupleType(memory, subType);
        for(Method m : ParametrizedTupleType.class.getMethods()){
            if(m.isAnnotationPresent(NativeInstanceMethod.class)) {
                Pair<ToolMethod, Boolean> resultPair = memory.baseTypes().loadNativeMethod(memory, m);
                result.addMethod(resultPair.getFirst());
            }
        }
        return result;

    }

    @NativeClassMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolObject parametrizeCardinality(@MemoryParameter Memory memory,
                                                    @SelfParameter ToolClass selfTupleClass,
                                                    ToolInteger cardinality) throws ToolNativeException{
        ParametrizedTupleType result = new ParametrizedTupleType(
                memory, memory.baseTypes().C_OBJECT, cardinality.getIntegerValue());
        for(Method m : ParametrizedTupleType.class.getMethods()){
            if(m.isAnnotationPresent(NativeInstanceMethod.class)) {
                Pair<ToolMethod, Boolean> resultPair = memory.baseTypes().loadNativeMethod(memory, m);
                result.addMethod(resultPair.getFirst());
            }
        }
        return result;
    }

    public static class ParametrizedTupleType extends TypeAdapter {
        private final ToolType parameterType;
        private final ToolClass tupleClass;
        private final Integer cardinality;

        public ParametrizedTupleType(Memory m, ToolType parameterType) {
            super(m, m.baseTypes().C_OBJECT);
            this.tupleClass = m.baseTypes().C_TUPLE;
            this.parameterType = parameterType;
            this.cardinality = null;
        }

        public ParametrizedTupleType(Memory m, Integer cardinality) {
            super(m, m.baseTypes().C_OBJECT);
            this.tupleClass = m.baseTypes().C_TUPLE;
            this.parameterType = m.baseTypes().C_OBJECT;
            this.cardinality = cardinality;
        }

        public ParametrizedTupleType(Memory m, ToolType parameterType, Integer cardinality){
            super(m, m.baseTypes().C_OBJECT);
            this.tupleClass = m.baseTypes().C_TUPLE;
            this.parameterType = parameterType;
            this.cardinality = cardinality;
        }

        public ParametrizedTupleType(Memory m, ToolClass tupleClass, ToolType parameterType, Integer cardinality){
            super(m, m.baseTypes().C_OBJECT);
            this.tupleClass = tupleClass;
            this.parameterType = parameterType;
            this.cardinality = cardinality;
        }

        public ToolType getParameterType() {
            return parameterType;
        }

        @Override
        @NativeInstanceMethod(value = "typeName", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
        public String getTypeName() throws ToolNativeException {
            StringBuilder sb = new StringBuilder(tupleClass.getTypeName());
            if(cardinality != null) sb.append("[").append(cardinality).append("]");
            sb.append("<").append(parameterType.getTypeName()).append(">");
            return sb.toString();
        }

        @Override
        @NativeInstanceMethod(value = "is", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                mode = ToolOperatorMethod.Mode.Binary)
        public boolean isOperator(ToolObject o) throws ToolNativeException {
            if(!memory.baseTypes().C_TUPLE.isOperator(o)) return false;
            ToolTuple tuple = (ToolTuple) o;
            if(cardinality!=null && cardinality != tuple.size()) return false;
            for (ToolObject object : tuple.getTupleObjects()) {
                if(!parameterType.isOperator(object)) return false;
            }
            return true;
        }


        @Override
        @NativeInstanceMethod
        public boolean canBeUsedAs(ToolType other) throws ToolNativeException {
            if(tupleClass.canBeUsedAs(other)) return true;
            if(other.canBeUsedAs(tupleClass)) {
                if(other instanceof ParametrizedTupleType){
                    ParametrizedTupleType o = (ParametrizedTupleType) other;
                    if(cardinality!=null && !Objects.equals(cardinality, o.cardinality)) return false;
                    return this.getParameterType().canBeUsedAs(o.getParameterType());
                }
            }
            return false;
        }

        @Override
        @NativeInstanceMethod
        public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
            int total = memory.baseTypes().C_TUPLE.getObjectConvertibility(from);
            if(from instanceof ToolTuple){
                ToolType fromParametricType = memory.baseTypes().nativeAlgorithms.getSupEnclosingClass(memory, ((ToolTuple) from).getTupleObjects());
                total += parameterType.getConvertibility(fromParametricType);
            }
            return total;
        }

        @Override
        @NativeInstanceMethod
        public int getConvertibility(ToolType from) throws ToolNativeException {
            int total = tupleClass.getConvertibility(from);
            if(from instanceof ParametrizedTupleType){
                ParametrizedTupleType o = (ParametrizedTupleType) from;
                total += this.getParameterType().getConvertibility(o.getParameterType());
            }
            return total;
        }

        @NativeInstanceMethod(value = "<>", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                mode = ToolOperatorMethod.Mode.BinaryParametric)
        public ToolObject parametrizeType(ToolType subType) throws ToolNativeException{
            ParametrizedTupleType result = new ParametrizedTupleType(memory, tupleClass, subType, cardinality);
            for(Method m : ParametrizedTupleType.class.getMethods()){
                if(m.isAnnotationPresent(NativeInstanceMethod.class)) {
                    Pair<ToolMethod, Boolean> resultPair = memory.baseTypes().loadNativeMethod(memory, m);
                    result.addMethod(resultPair.getFirst());
                }
            }
            return result;

        }

        @NativeInstanceMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                mode = ToolOperatorMethod.Mode.BinaryParametric)
        public ToolObject parametrizeCardinality(ToolInteger cardinality) throws ToolNativeException{
            ParametrizedTupleType result = new ParametrizedTupleType(
                    memory, tupleClass, parameterType, cardinality.getIntegerValue());
            for(Method m : ParametrizedTupleType.class.getMethods()){
                if(m.isAnnotationPresent(NativeInstanceMethod.class)) {
                    Pair<ToolMethod, Boolean> resultPair = memory.baseTypes().loadNativeMethod(memory, m);
                    result.addMethod(resultPair.getFirst());
                }
            }
            return result;
        }
    }
}
