package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.NullValueException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 06/06/16.
 * TODO: javadoc
 */
public class ToolOptional<T extends ToolObject> extends ToolObject{
    private final ToolObject object;

    public ToolOptional(Memory m, T object){
        super(m, m.baseTypes().C_OPTIONAL);
        this.object = object;
    }

    public ToolOptional(Memory m){
        super(m, m.baseTypes().C_OPTIONAL);
        this.object = m.baseTypes().O_NULL;
    }

    public ToolObject getObject() {
        return object;
    }

    @NativeInstanceMethod(value = "isNull", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolBoolean isNull(@MemoryParameter Memory memory,
                                     @ImplicitParameter ToolOptional self){
        return new ToolBoolean(memory, self.object.isNull());
    }

    @NativeInstanceMethod(value = "()", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolObject roundBrackets(@MemoryParameter Memory memory,
                                   @ImplicitParameter ToolOptional self) throws NullValueException {
        return value(memory, self);
    }

    @NativeInstanceMethod(value = "value", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolObject value(@MemoryParameter Memory memory,
                                           @ImplicitParameter ToolOptional self) throws NullValueException {
        if(self.object.isNull()){
            throw new NullValueException(memory, "");
        }
        return self.object;
    }
}
