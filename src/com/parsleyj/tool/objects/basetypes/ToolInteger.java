package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolArithmeticException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolInteger extends ToolObject {
    private Integer integer;

    public ToolInteger(Integer integer) {
        super(BaseTypes.C_INTEGER);
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
    public static ToolInteger _plus_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolInteger(a.getIntegerValue() + b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "-", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _minus_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolInteger(a.getIntegerValue() - b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "*", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _asterisk_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolInteger(a.getIntegerValue() * b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "/", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _slash_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException("Division by zero.");
        return new ToolInteger(a.getIntegerValue() / b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "%", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolInteger _percentSign_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException("Division by zero.");
        return new ToolInteger(a.getIntegerValue() % b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "-", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Prefix)
    public static ToolInteger _unaryMinus_(@ImplicitParameter ToolInteger a){
        return new ToolInteger(-a.getIntegerValue());
    }

    @NativeInstanceMethod(value = ">", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _greater_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() > b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "<", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _less_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() < b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "==", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equals_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }

    @NativeInstanceMethod(value = ">=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equalsOrGreater_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() >= b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "<=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _equalsOrLess_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() <= b.getIntegerValue());
    }

    @NativeInstanceMethod(value = "!=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolBoolean _notEquals_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        return new ToolBoolean(!Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }

    @NativeInstanceMethod(value = "to", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolList _to_(@ImplicitParameter ToolInteger a, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolInteger b){
        List<ToolInteger> toolIntegerList = new ArrayList<>();
        if(a.getIntegerValue() <= b.getIntegerValue()){
            for(int i = a.getIntegerValue(); i < b.getIntegerValue(); ++i){
                toolIntegerList.add(new ToolInteger(i));
            }
        }else /*if (a.getIntegerValue() > b.getIntegerValue())*/{
            for(int i = a.getIntegerValue(); i > b.getIntegerValue(); --i){
                toolIntegerList.add(new ToolInteger(i));
            }
        }
        return new ToolList(toolIntegerList);
    }



    @Override
    public String getPrintString() {
        return ""+String.valueOf(integer);
    }

    public void setIntegerValue(Integer integerValue) {
        this.integer = integerValue;
    }
}
