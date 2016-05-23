package com.parsleyj.tool.semantics.base;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public interface RValue extends SemanticObject{
    public static final String COMPLEX_EXPRESSION = "COMPLEX_EXPRESSION";

    default String getExpressionCategory(){
        return COMPLEX_EXPRESSION;
    }

    ToolObject evaluate(Memory memory) throws ToolNativeException;

    public static boolean evaluateAsConditional(RValue r, Memory m) throws ToolNativeException {
        ToolObject to = r.evaluate(m);
        return to.evaluateAsConditional(m);
    }
}
