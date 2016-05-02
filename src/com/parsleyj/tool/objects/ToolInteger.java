package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolArithmeticException;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;

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
        return "<Integer:" + String.valueOf(integer) + ">";
    }

    public Integer getIntegerValue() {
        return integer;
    }


    @NativeInstanceMethod
    public static ToolInteger _plus_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(a.getIntegerValue() + b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolInteger _minus_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(a.getIntegerValue() - b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolInteger _asterisk_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolInteger(a.getIntegerValue() * b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolInteger _slash_(@SelfParameter ToolInteger a, ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException("Division by zero.");
        return new ToolInteger(a.getIntegerValue() / b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolInteger _percentSign_(@SelfParameter ToolInteger a, ToolInteger b) throws ToolArithmeticException {
        if((b.getIntegerValue() == 0)) throw new ToolArithmeticException("Division by zero.");
        return new ToolInteger(a.getIntegerValue() % b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _greater_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() > b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _less_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() < b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _equals_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }

    @NativeInstanceMethod
    public static ToolBoolean _equalsOrGreater_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() >= b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _equalsOrLess_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(a.getIntegerValue() <= b.getIntegerValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _notEquals_(@SelfParameter ToolInteger a, ToolInteger b){
        return new ToolBoolean(!Objects.equals(a.getIntegerValue(), b.getIntegerValue()));
    }



    @Override
    public String getPrintString() {
        return ""+integer;
    }
}
