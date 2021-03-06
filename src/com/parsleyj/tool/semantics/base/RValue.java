package com.parsleyj.tool.semantics.base;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public interface RValue extends SemanticObject{

    ToolObject evaluate(Memory memory) throws ToolNativeException;

    default boolean evaluateAsConditional(Memory m) throws ToolNativeException {
        ToolObject to = evaluate(m);
        return to.evaluateAsConditional(m);
    }

    default void addTag(String s){
        //does nothing by default. override this to use tags in specific expressions.
    }
}
