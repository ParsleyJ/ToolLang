package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolOptional;
import com.parsleyj.tool.objects.ToolType;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 05/06/16.
 * TODO: javadoc
 */
public class ToolMethodSet extends ToolObject {

    private final ToolObject ownerObject;
    private final String name;

    public ToolMethodSet(Memory m, ToolObject ownerObject, String name) {
        super(m, m.baseTypes().C_METHOD_SET);
        this.ownerObject = ownerObject;
        this.name = name;
    }

    @NativeInstanceMethod(value = "<>", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolOptional<ToolMethod> resolveByTypes(@MemoryParameter Memory memory,
                                            @ImplicitParameter ToolMethodSet self,
                                            ToolList typeList) throws ToolNativeException {
        //TODO: use is + <> operators on typeList to check if is list of Typesx
        List<ToolType> types = typeList.getToolObjects().stream().map(object -> (ToolType) object).collect(Collectors.toList());
        MethodTable table = self.ownerObject.generateCallableMethodTable();
        return table.resolveByTypes(
                ToolMethod.METHOD_CATEGORY_METHOD,
                self.name,
                types);
    }

    @NativeInstanceMethod(value = "()", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolMethod resolveByObjects(@MemoryParameter Memory memory,
                                              @ImplicitParameter ToolMethodSet self,
                                              ToolList objectList) throws ToolNativeException {
        List<ToolObject> arguments = objectList.getToolObjects();
        MethodTable table = self.ownerObject.generateCallableMethodTable();
        return  table.resolve(
                self.ownerObject,
                ToolMethod.METHOD_CATEGORY_METHOD,
                self.name,
                arguments
        );
    }


}
