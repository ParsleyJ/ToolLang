package com.parsleyj.tool.objects;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.List;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class ToolTuple extends ToolObject {
    private final List<ToolObject> objects;

    public ToolTuple(Memory m, List<ToolObject> objects) {
        super(m, m.baseTypes().C_TUPLE);
        this.objects = objects;
    }

    public List<ToolObject> getTupleObjects() {
        return objects;
    }

    @NativeInstanceMethod(value = ":=", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.Binary)
    public static ToolObject destGetter(@MemoryParameter Memory memory, @ImplicitParameter ToolTuple self, ToolInteger arg){
        return self.objects.get(arg.getIntegerValue());
    }
}
