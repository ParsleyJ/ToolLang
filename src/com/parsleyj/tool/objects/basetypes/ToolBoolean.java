package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolBoolean extends ToolObject {
    private boolean x;
    public ToolBoolean(boolean x){
        super(BaseTypes.C_BOOLEAN);
        this.x = x;
    }

    public boolean getBoolValue(){
        return x;
    }


    @Override
    public boolean evaluateAsConditional(Memory memory) throws ToolNativeException {
        return getBoolValue();
    }

    @NativeInstanceMethod
    public static ToolBoolean _logicalNot_(@ImplicitParameter ToolBoolean a){
        return new ToolBoolean(!a.getBoolValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _logicalAnd_(@ImplicitParameter ToolBoolean a, ToolBoolean b){
        return new ToolBoolean(a.getBoolValue() && b.getBoolValue());
    }

    @NativeInstanceMethod
    public static ToolBoolean _logicalOr_(@ImplicitParameter ToolBoolean a, ToolBoolean b){
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
