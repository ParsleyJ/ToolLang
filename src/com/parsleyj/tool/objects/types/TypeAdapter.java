package com.parsleyj.tool.objects.types;


import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolType;

/**
 * Created by Giuseppe on 09/06/16.
 * TODO: javadoc
 */
public abstract class TypeAdapter extends ToolObject implements ToolType {


    public TypeAdapter(Memory m, ToolClass belongingClass) {
        super(m, belongingClass);
    }
}
