package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolException extends ToolObject {
    private String explain;

    public ToolException(String explain) {
        super(BaseTypes.C_EXCEPTION);
        this.explain = explain;
    }

    public ToolException(ToolExceptionClass superClass, String explain){
        super(superClass);
        this.explain = explain;
    }

    public String getExplain() {
        return explain;
    }

    @Override
    public String toString() {
        return "<Exception:" + explain + ">";
    }
}
