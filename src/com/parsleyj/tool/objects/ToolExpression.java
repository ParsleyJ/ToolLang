package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 31/08/16.
 * TODO: javadoc
 */
public class ToolExpression extends ToolObject {
    private RValue expression;
    public ToolExpression(Memory m, RValue expression) {
        super(m, m.baseTypes().C_EXPRESSION);
        this.expression = expression;
    }

    @NativeInstanceMethod(value = "()", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public ToolObject call(@MemoryParameter Memory m) throws ToolNativeException{
        return expression.evaluate(m);
    }

}
