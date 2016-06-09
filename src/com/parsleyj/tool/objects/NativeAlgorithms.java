package com.parsleyj.tool.objects;

import com.parsleyj.tool.memory.Memory;

import java.util.List;

/**
 * Created by Giuseppe on 09/06/16.
 * TODO: javadoc
 */
public class NativeAlgorithms {
    public ToolClass getSupEnclosingClass(Memory memory, List<ToolObject> list){
        ToolClass result = memory.baseTypes().C_OBJECT;
        for (ToolObject object : list) {
            if(object.getBelongingClass().isOrExtends(result) && !object.getBelongingClass().isExactly(result)){
                result = object.getBelongingClass();
            }
        }
        return result;
    }
}
