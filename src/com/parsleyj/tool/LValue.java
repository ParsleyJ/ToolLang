package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public interface LValue extends RValue {
    void assign(ToolObject o, Memory m) throws ToolInternalException;
}
