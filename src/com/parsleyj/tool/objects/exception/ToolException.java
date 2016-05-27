package com.parsleyj.tool.objects.exception;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolException extends ToolObject {
    private String explain;

    public ToolException(Memory m, String explain) {
        super(m, m.baseTypes().C_EXCEPTION);
        this.explain = explain;
    }

    public ToolException(Memory m, ToolExceptionClass superClass, String explain){
        super(m, superClass);
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
