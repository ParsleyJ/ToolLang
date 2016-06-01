package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValueList;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.util.MethodCall;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class ElementAccessOperation implements RValue {

    private RValue left;
    private RValueList indexes;

    public ElementAccessOperation(RValue left, RValueList indexes){
        this.left = left;
        this.indexes = indexes;
    }

    public ElementAccessOperation(RValue left, RValue index, boolean singleElement){
        this.left = left;
        this.indexes = new RValueList(index);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolList toolList = indexes.generateToolList(memory);
        return MethodCall.binaryParametricOperator(left, "[", toolList, "]").evaluate(memory);
    }
}
