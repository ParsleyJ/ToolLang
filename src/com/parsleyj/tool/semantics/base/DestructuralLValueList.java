package com.parsleyj.tool.semantics.base;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public interface DestructuralLValueList {
    void multiAssignOrdinal(ToolObject o, Memory m) throws ToolNativeException;
}
