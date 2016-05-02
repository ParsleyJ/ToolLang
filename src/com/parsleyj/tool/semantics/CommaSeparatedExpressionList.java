package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolList;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class CommaSeparatedExpressionList implements SemanticObject {
    private ArrayList<RValue> unevaluatedList = new ArrayList<>();

    public CommaSeparatedExpressionList(RValue unique){
        unevaluatedList.add(unique);
    }

    public CommaSeparatedExpressionList(RValue a, RValue b) {
        unevaluatedList.add(a);
        unevaluatedList.add(b);
    }

    public CommaSeparatedExpressionList(CommaSeparatedExpressionList a, RValue b) {
        unevaluatedList.addAll(a.getUnevaluatedList());
        unevaluatedList.add(b);
    }

    public ArrayList<RValue> getUnevaluatedList() {
        return unevaluatedList;
    }

    public RValue[] getUnevaluatedArray(){
        return getUnevaluatedList().toArray(new RValue[unevaluatedList.size()]);
    }
    public ToolList generateToolList(Memory m) throws ToolNativeException {
        return new ToolList(generateListOfObjects(m));
    }

    public ToolObject[] generateArrayOfObjects(Memory m) throws ToolNativeException {
        ArrayList<ToolObject> list = generateListOfObjects(m);
        return list.toArray(new ToolObject[list.size()]);
    }

    public ArrayList<ToolObject> generateListOfObjects(Memory m) throws ToolNativeException {
        ArrayList<ToolObject> list = new ArrayList<>();
        for (RValue r : unevaluatedList) {
            list.add(r.evaluate(m));
        }
        return list;
    }

}
