package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolExpression;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolBoolean extends ToolObject {
    private boolean x;
    public ToolBoolean(Memory m, boolean x){
        super(m, m.baseTypes().C_BOOLEAN);
        this.x = x;
    }

    public boolean getBoolValue(){
        return x;
    }


    @Override
    public boolean evaluateAsConditional(Memory memory) throws ToolNativeException {
        return getBoolValue();
    }

    @NativeInstanceMethod(value = "!", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Prefix)
    public ToolBoolean _logicalNot_(@MemoryParameter Memory m){
        return new ToolBoolean(m, !this.getBoolValue());
    }

    @NativeInstanceMethod(value = "and", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Binary)
    public ToolBoolean _logicalAnd_(@MemoryParameter Memory m, ToolExpression b) throws ToolNativeException{
        if(!this.getBoolValue()) return new ToolBoolean(m, false);
        if(b.call(m).evaluateAsConditional(m)){
            return new ToolBoolean(m, true);
        }else{
            return new ToolBoolean(m, false);
        }

    }

    @NativeInstanceMethod(value = "or", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Binary)
    public ToolBoolean _logicalOr_(@MemoryParameter Memory m, ToolExpression b) throws ToolNativeException {
        if(this.getBoolValue()) return new ToolBoolean(m, true);
        if(b.call(m).evaluateAsConditional(m)){
            return new ToolBoolean(m, true);
        }else{
            return new ToolBoolean(m, false);
        }

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
