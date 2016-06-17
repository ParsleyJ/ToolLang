package com.parsleyj.tool.semantics.tuples;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolTuple;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.base.RValueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class RValueListBetweenBrackets implements RValue {


    private ArrayList<RValue> rValues = new ArrayList<>();

    public RValueListBetweenBrackets(RValue unique){
        rValues.add(unique);
    }

    public RValueListBetweenBrackets(RValueList list){
        rValues.addAll(list.getUnevaluatedList());
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        List<ToolObject> objects = new ArrayList<>();
        for(RValue rValue : rValues)
            objects.add(rValue.evaluate(memory));

        return new ToolTuple(memory, objects);
    }


}
