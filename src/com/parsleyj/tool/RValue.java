package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.InvalidConditionalExpressionException;
import com.parsleyj.tool.exceptions.ToolInternalException;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public interface RValue extends SemanticObject{
    public static boolean evaluateAsConditional(RValue r, Memory m) throws ToolInternalException {
        ToolObject to = r.evaluate(m);
        if(to.getBelongingClass().isOrExtends(BaseTypes.C_BOOLEAN)){
            return (Boolean) to.getNativeValue();
        }
        else throw new InvalidConditionalExpressionException("The expression: ("+r+") did not return a Boolean value");

    }
    ToolObject evaluate(Memory memory) throws ToolInternalException;
}
