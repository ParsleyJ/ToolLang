package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.utils.PJ;

import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolString extends ToolObject {
    private String string;

    public static ToolString newFromLiteral(Memory m, String literal){
        String s1 = literal.substring(1, literal.length()-1);
        String s2 = unescape(s1);
        return new ToolString(m, s2);
    }

    public static String unescape(String s1) {
        return s1; //TODO: impl
    }

    public ToolString(Memory m, String string) {
        super(m, m.baseTypes().C_STRING);
        this.string = string;
    }

    public String getStringValue() {
        return string;
    }

    @Override
    public String getPrintString() {
        return string;
    }

    @Override
    public String toString() {
        return "\""+string+"\"";
    }

    @NativeInstanceMethod(value = "+", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolString _plus_(@MemoryParameter Memory m, @ImplicitParameter ToolString a, ToolString b){
        return new ToolString(m, a.getStringValue() + b.getStringValue());
    }

    @NativeInstanceMethod(value = "+", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolString _plus_(@MemoryParameter Memory m, @ImplicitParameter ToolString a, ToolObject b){
        return new ToolString(m, a.getStringValue() + b.getPrintString());
    }

    @NativeInstanceMethod(value = "/", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolList _slash_(@MemoryParameter Memory m, @ImplicitParameter ToolString a, ToolString delimiter){
        return new ToolList(m, PJ.list(a.getStringValue().split(delimiter.getStringValue()))
                .stream()
                .map((s) -> new ToolString(m, s))
                .collect(Collectors.toList())
        );
    }

}
