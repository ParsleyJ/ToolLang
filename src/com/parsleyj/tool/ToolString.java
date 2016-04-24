package com.parsleyj.tool;

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
        return s1;
    }

    public ToolString(String string) {
        super(BaseTypes.C_STRING);
        this.string = string;
    }

    @Override
    public String getPrintString() {
        return string;
    }

    @Override
    public String toString() {
        return "\""+string+"\"";
    }
}
