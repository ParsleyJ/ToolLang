package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.collection.ToolList;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class ElementAccessOperation implements RValue {

    private RValue left;
    private CommaSeparatedExpressionList indexes;

    public ElementAccessOperation(RValue left, CommaSeparatedExpressionList indexes){
        this.left = left;
        this.indexes = indexes;
    }

    public ElementAccessOperation(RValue left, RValue index, boolean singleElement){
        this.left = left;
        this.indexes = new CommaSeparatedExpressionList(index);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolList toolList = indexes.generateToolList(memory);
        return new MethodCall(left, "_elementAt_", new RValue[]{toolList}).evaluate(memory); //TODO add varargs function declaration
    }
}
