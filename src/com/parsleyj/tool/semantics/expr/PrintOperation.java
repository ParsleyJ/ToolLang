package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.BadReturnedValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.base.RValueList;
import com.parsleyj.tool.semantics.tuples.RValueListBetweenBrackets;
import com.parsleyj.tool.semantics.util.MethodCall;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 21/08/16.
 * TODO: javadoc
 */
public class PrintOperation implements RValue {
    private ArrayList<RValue> exprs = new ArrayList<>();

    public PrintOperation(RValue expr) {
        this.exprs.add(expr);
    }

    public PrintOperation(PrintOperation po, RValue expr2){
        exprs.addAll(po.exprs);
        exprs.add(expr2);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        RValue expr;
        if(exprs.size() == 1) expr = exprs.get(0);
        else expr = new RValueList(exprs).generateToolTuple(memory);
        ToolObject object = MethodCall.prefixOperator("print", expr).evaluate(memory);
        if(memory.baseTypes().C_STRING.isOperator(object)){
            ToolString toolString = (ToolString) object;
            String s = toolString.getStringValue();
            System.out.println(s);
            return toolString;
        }else{
            throw new BadReturnedValueException(memory, "print operator methods must always return String values.");
        }
    }
}
