package com.parsleyj.tool.objects;

import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;

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


    @NativeInstanceMethod(Visibility.Public)
    public static ToolBoolean _logicalNot_(@SelfParameter ToolBoolean a){
        return new ToolBoolean(!a.getBoolValue());
    }

    @NativeInstanceMethod(Visibility.Public)
    public static ToolBoolean _logicalAnd_(@SelfParameter ToolBoolean a, ToolBoolean b){
        return new ToolBoolean(a.getBoolValue() && b.getBoolValue());
    }

    @NativeInstanceMethod(Visibility.Public)
    public static ToolBoolean _logicalOr_(@SelfParameter ToolBoolean a, ToolBoolean b){
        return new ToolBoolean(a.getBoolValue() || b.getBoolValue());
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
