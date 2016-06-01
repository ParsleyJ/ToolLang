package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public class ToolMethodPrototype extends ToolObject {

    protected List<ToolClass> argumentTypes = new ArrayList<>();
    protected String methodCategory;
    protected String name;

    public ToolMethodPrototype(Memory m, String category, String name, List<ToolClass> argumentTypes) {
        super(m, m.baseTypes().C_METHOD_PROTOTYPE);
        this.argumentTypes.addAll(argumentTypes);
        this.methodCategory = category;
        this.name = name;
    }

    //used by ToolMethod class.
    protected ToolMethodPrototype(Memory m, ToolClass toolClass) {
        super(m, toolClass);
    }


    public String getMethodCategory() {
        return methodCategory;
    }

    public String getMethodName() {
        return name;
    }

    public List<ToolClass> getArgumentTypes() {
        return argumentTypes;
    }

    public Visibility getVisibility(){
        return Visibility.Public;
    }
}
