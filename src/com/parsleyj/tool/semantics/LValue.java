package com.parsleyj.tool.semantics;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public interface LValue extends RValue {
    void assign(ToolObject o, Memory m) throws ToolInternalException;
}
