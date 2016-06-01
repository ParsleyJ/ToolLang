package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.DestructuralLValueList;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.tuples.LValueListBetweenBrackets;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class DestructuralAssignment implements RValue {
    private LValue lValue;
    private RValue rValue;

    public DestructuralAssignment(LValue lValue, RValue rValue) {
        this.lValue = lValue;
        this.rValue = rValue;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject rObject = rValue.evaluate(memory);
        if(lValue instanceof DestructuralLValueList){
            ((DestructuralLValueList) lValue).multiAssignOrdinal(rObject, memory);
        } else {
            new LValueListBetweenBrackets(lValue).multiAssignOrdinal(rObject, memory);
        }
        return rObject;
    }
}
