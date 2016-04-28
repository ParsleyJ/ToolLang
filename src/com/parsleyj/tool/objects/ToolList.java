package com.parsleyj.tool.objects;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolList extends ToolObject {
    public ArrayList<ToolObject> toolObjectArrayList;

    public ToolList(ArrayList<ToolObject> list) {
        super(BaseTypes.C_LIST);
        toolObjectArrayList = list;
    }

    //TODO add(index, obj)
    //TODO addLast(obj)
    //TODO addFirst(obj)
    //TODO addAll(list)
    //TODO remove(index)
    //TODO removeLast()
    //TODO removeFirst()
    //TODO removeAll()
    //TODO getLast()
    //TODO getFirst()
    //TODO get(index)

}
