package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolArithmeticException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.Objects;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolInteger extends ToolObject {
    private Integer integer;

    public ToolInteger(Memory m, Integer integer) {
        super(m, m.baseTypes().C_INTEGER);
        this.integer = integer;
    }

    @Override
    public String toString() {
        return ""+String.valueOf(integer);
    }

    public Integer getIntegerValue() {
        return integer;
    }


    @NativeInstanceMethod(value = "+", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _plus_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(m, a.getIntegerValue() + b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "-", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _minus_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(m, a.getIntegerValue() - b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "*", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _asterisk_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(m, a.getIntegerValue() * b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "/", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _slash_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException(m, "Division by zero.");
        return new ToolInteger(m, a.getIntegerValue() / b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "%", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _percentSign_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException(m, "Division by zero.");
        return new ToolInteger(m, a.getIntegerValue() % b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "-", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Prefix)
    public static ToolInteger _unaryMinus_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a){
        return new ToolInteger(m, -a.getIntegerValue());
    }

    @NativeInstanceMethod(value = ">", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _greater_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, a.getIntegerValue() > b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "<", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _less_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, a.getIntegerValue() < b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "==", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equals_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }

    @NativeInstanceMethod(value = ">=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equalsOrGreater_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, a.getIntegerValue() >= b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "<=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equalsOrLess_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, a.getIntegerValue() <= b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "!=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _notEquals_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(m, !Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }

    @NativeInstanceMethod(value = "to", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolIntegerRange _to_(@MemoryParameter Memory m, @ImplicitParameter ToolInteger a, ToolInteger b){
        return new ToolIntegerRange(m, a.getIntegerValue(), b.getIntegerValue());
    }



    @Override
    public String getPrintString() {
        return ""+String.valueOf(integer);
    }

    public void setIntegerValue(Integer integerValue) {
        this.integer = integerValue;
    }
}
