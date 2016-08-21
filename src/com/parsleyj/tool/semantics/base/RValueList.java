package com.parsleyj.tool.semantics.base;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolTuple;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class RValueList implements SemanticObject {

    private ArrayList<RValue> unevaluatedList = new ArrayList<>();

    public RValueList(RValue unique){
        unevaluatedList.add(unique);
    }

    public RValueList(RValue a, RValue b) {
        unevaluatedList.add(a);
        unevaluatedList.add(b);
    }

    public RValueList(RValueList a, RValue b) {
        unevaluatedList.addAll(a.getUnevaluatedList());
        unevaluatedList.add(b);
    }

    public RValueList(List<RValue> list){
        unevaluatedList.addAll(list);
    }

    public ArrayList<RValue> getUnevaluatedList() {
        return unevaluatedList;
    }

    public RValue[] getUnevaluatedArray(){
        return getUnevaluatedList().toArray(new RValue[unevaluatedList.size()]);
    }

    public ToolList generateToolList(Memory m) throws ToolNativeException {
        return new ToolList(m, generateListOfObjects(m));
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

    public ToolTuple generateToolTuple(Memory mem) throws ToolNativeException{
        ArrayList<ToolObject> objects = new ArrayList<>();
        for (RValue rValue : unevaluatedList) {
            objects.add(rValue.evaluate(mem));
        }
        return new ToolTuple(mem, objects);
    }

}
