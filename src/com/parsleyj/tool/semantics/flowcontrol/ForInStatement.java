package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.InvalidIterableExpressionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.util.MethodCall;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ForInStatement implements RValue {
    private final Identifier identifier;
    private final RValue iterableExpression;
    private final RValue doBranch;

    public ForInStatement(Identifier identifier, RValue iterableExpression, RValue doBranch) {
        this.identifier = identifier;
        this.iterableExpression = iterableExpression;
        this.doBranch = doBranch;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject iterable = iterableExpression.evaluate(memory);
        if(!iterable.getBelongingClass().implementsInterface(memory.baseTypes().I_ITERABLE))
            throw new InvalidIterableExpressionException(memory, "The expression "+iterableExpression+" does not return an Iterable");
        ToolObject iterator = MethodCall.getter(iterable, "iterator").evaluate(memory);
        ToolObject result = memory.baseTypes().O_NULL;
        memory.pushScope();
        memory.newLocalReference(identifier.getIdentifierString(), memory.baseTypes().O_NULL);
        while(((ToolBoolean) MethodCall.getter(iterator, "hasNext").evaluate(memory)).getBoolValue()){
            memory.updateReference(identifier.getIdentifierString(), MethodCall.getter(iterator, "next").evaluate(memory));
            result = MethodCall.executeScopedBlockWithNoParameters(doBranch, memory);
        }
        memory.popScope();
        return result;
    }
}
