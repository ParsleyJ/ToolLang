package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolArithmeticException;

import java.util.Arrays;
import java.util.Collections;
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
        return "<Integer:" + String.valueOf(integer) + ">";
    }

    public Integer getInteger() {
        return integer;
    }

    public static List<ToolMethod> getNativeClassMethods() {
        return Collections.emptyList();
    }

    public static List<ToolMethod> getNativeInstanceMethods() {
        return Arrays.asList(
                new CBONativeInstanceMethod<ToolInteger>(
                        "_asterisk_", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() * b.getInteger())),
                new CBONativeInstanceMethod<ToolInteger>(
                        "_slash_", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> {
                            if((b.getInteger() == 0)) throw new ToolArithmeticException("Division by zero.");
                            return new ToolInteger(a.getInteger() / b.getInteger());
                        }),
                new CBONativeInstanceMethod<ToolInteger>(
                        "_percentSign_", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> {
                            if((b.getInteger() == 0)) throw new ToolArithmeticException("Division by zero.");
                            return new ToolInteger(a.getInteger() % b.getInteger());
                        }),
                new CBONativeInstanceMethod<ToolInteger>(
                        "_plus_", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() + b.getInteger())),
                new CBONativeInstanceMethod<ToolInteger>(
                        "_minus_", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() - b.getInteger())),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_greater_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(a.getInteger() > b.getInteger())),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_equalsOrGreater_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(a.getInteger() >= b.getInteger())),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_less_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(a.getInteger() < b.getInteger())),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_equalsOrLess_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(a.getInteger() <= b.getInteger())),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_equals_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(Objects.equals(a.getInteger(), b.getInteger()))),
                new UBONativeInstanceMethod<ToolBoolean, ToolInteger, ToolInteger>(
                        "_notEquals_", BaseTypes.C_INTEGER, BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolBoolean(!Objects.equals(a.getInteger(), b.getInteger())))
        );
    }

    @Override
    public String getPrintString() {
        return ""+integer;
    }
}
