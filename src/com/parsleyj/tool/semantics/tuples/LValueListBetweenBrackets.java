package com.parsleyj.tool.semantics.tuples;

import com.parsleyj.tool.exceptions.InvalidLValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.semantics.base.DestructuralLValueList;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.LValueList;
import com.parsleyj.tool.semantics.base.NamedLValue;
import com.parsleyj.tool.semantics.nametabled.ObjectDotIdentifier;
import com.parsleyj.tool.semantics.util.MethodCall;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class LValueListBetweenBrackets extends RValueListBetweenBrackets implements LValue, DestructuralLValueList {

    private ArrayList<LValue> lValues = new ArrayList<>();

    public LValueListBetweenBrackets(LValue unique){
        super(unique);
        lValues.add(unique);
    }

    public LValueListBetweenBrackets(LValueList list){
        super(list);
        lValues.addAll(list.getLvlist());
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        multiAssignNamely(o, m);
    }

    @Override
    public void multiAssignOrdinal(ToolObject o, Memory m) throws ToolNativeException {
        for (int i = 0; i < lValues.size(); i++) {
            LValue lValue = lValues.get(i);
            lValue.assign(MethodCall.binaryOperator(o, ":=", new ToolInteger(m, i)).evaluate(m), m);
        }
    }

    public void multiAssignNamely(ToolObject o, Memory m) throws ToolNativeException{
        for(LValue lValue : lValues){
            if(lValue instanceof NamedLValue){
                lValue.assign(new ObjectDotIdentifier(o, ((NamedLValue) lValue).getIdentifierString()).evaluate(m), m);
            }else throw new InvalidLValueException(m, "'"+lValue+"' is not a named l-value");
        }
    }
}
