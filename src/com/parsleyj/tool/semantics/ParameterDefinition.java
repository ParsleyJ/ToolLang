package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

/**
 * Created by Giuseppe on 19/05/16.
 * TODO: javadoc
 */
public interface ParameterDefinition extends SemanticObject {
    FormalParameter define(Memory memory) throws ToolNativeException;
}
