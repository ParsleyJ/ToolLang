package com.parsleyj.tool.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolBoolean extends ToolObject{
    private boolean x;
    public ToolBoolean(boolean x){
        super(BaseTypes.C_BOOLEAN);
        this.x = x;
    }

    public boolean getBoolValue(){
        return x;
    }


    public static List<ToolMethod> getNativeClassMethods() {
        return Collections.emptyList();
    }

    public static List<ToolMethod> getNativeInstanceMethods() {
        return Arrays.asList(
                new PUONativeInstanceMethod<ToolBoolean>("_logicalNot_", BaseTypes.C_BOOLEAN,
                        (a, mem) -> new ToolBoolean(!a.getBoolValue())),
                new CBONativeInstanceMethod<ToolBoolean>("_logicalAnd_", BaseTypes.C_BOOLEAN, "x", //TODO: short-circuit
                        (a, b, mem)-> new ToolBoolean(a.getBoolValue() && b.getBoolValue())),
                new CBONativeInstanceMethod<ToolBoolean>("_logicalOr_", BaseTypes.C_BOOLEAN, "x", //TODO: short-circuit
                        (a, b, mem)-> new ToolBoolean(a.getBoolValue() || b.getBoolValue()))
        );
    }

    @Override
    public String toString() {
        return "<Boolean:"+x+">";
    }

    @Override
    public String getPrintString() {
        return ""+x;
    }
}
