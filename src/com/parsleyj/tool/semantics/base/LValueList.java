package com.parsleyj.tool.semantics.base;

import com.parsleyj.tool.exceptions.InvalidLValueException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.nametabled.*;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public class LValueList extends RValueList {

    private ArrayList<LValue> lvlist = new ArrayList<>();

    public LValueList(LValueList a, LValue b) {
        super(a, b);
        lvlist.addAll(a.getLvlist());
        lvlist.add(b);
    }

    public LValueList(LValue a, LValue b) {
        super(a, b);
        lvlist.add(a);
        lvlist.add(b);
    }

    public ArrayList<LValue> getLvlist() {
        return lvlist;
    }


}
