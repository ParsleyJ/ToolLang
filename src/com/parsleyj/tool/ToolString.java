package com.parsleyj.tool;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolString extends ToolObject {
    private String string;

    public ToolString(String string) {
        super(BaseTypes.C_STRING, string);
        this.string = string;
    }

    @Override
    public String toString() {
        return "\""+string+"\"";
    }
}
