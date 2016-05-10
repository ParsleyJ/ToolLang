package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.utils.PJ;

import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolString extends ToolObject {
    private String string;

    public static ToolString newFromLiteral(String literal){
        String s1 = literal.substring(1, literal.length()-1);
        String s2 = unescape(s1);
        return new ToolString(s2);
    }

    public static String unescape(String s1) {
        return s1; //TODO: impl
    }

    public ToolString(String string) {
        super(BaseTypes.C_STRING);
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

    @NativeInstanceMethod
    public static ToolString _plus_(@ImplicitParameter ToolString a, ToolString b){
        return new ToolString(a.getStringValue() + b.getStringValue());
    }

    @NativeInstanceMethod
    public static ToolString _plus_(@ImplicitParameter ToolString a, ToolObject b){
        return new ToolString(a.getStringValue() + b.getPrintString());
    }

    @NativeInstanceMethod
    public static ToolList _slash_(@ImplicitParameter ToolString a, ToolString delimiter){
        return new ToolList(PJ.list(a.getStringValue().split(delimiter.getStringValue()))
                .stream()
                .map(ToolString::new)
                .collect(Collectors.toList())
        );
    }

}
