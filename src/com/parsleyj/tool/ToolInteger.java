package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolArithmeticException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                new CBONativeInstanceMethod<ToolInteger>("asterisk", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() * b.getInteger())),
                new CBONativeInstanceMethod<ToolInteger>("slash", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> {
                            if((b.getInteger() == 0)) throw new ToolArithmeticException("Division by zero.");
                            return new ToolInteger(a.getInteger() / b.getInteger());
                        }),
                new CBONativeInstanceMethod<ToolInteger>("percentSign", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> {
                            if((b.getInteger() == 0)) throw new ToolArithmeticException("Division by zero.");
                            return new ToolInteger(a.getInteger() % b.getInteger());
                        }),
                new CBONativeInstanceMethod<ToolInteger>("plus", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() + b.getInteger())),
                new CBONativeInstanceMethod<ToolInteger>("minus", BaseTypes.C_INTEGER, "x",
                        (a,b,mem) -> new ToolInteger(a.getInteger() - b.getInteger()))
        );
    }

    @Override
    public String getPrintString() {
        return ""+integer;
    }
}
