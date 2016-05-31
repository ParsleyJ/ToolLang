package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.InvalidIterableExpressionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.expr.Assignment;
import com.parsleyj.tool.semantics.nametabled.DefinitionVariable;
import com.parsleyj.tool.semantics.nametabled.LocalAtIdentifier;
import com.parsleyj.tool.semantics.util.MethodCall;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ForInStatement implements RValue {
    public static final String LOOP_SCOPE_TAG = "loop";
    public static final String FOR_LOOP_SCOPE_TAG = "for";
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
        memory.getTopScope().addTag(BreakStatement.BREAKABLE_SCOPE_TAG);
        memory.getTopScope().addTag(LOOP_SCOPE_TAG);
        memory.getTopScope().addTag(FOR_LOOP_SCOPE_TAG);
        new DefinitionVariable(identifier.getIdentifierString()).assign(memory.baseTypes().O_NULL, memory);
        try {
            while(((ToolBoolean) MethodCall.getter(iterator, "hasNext").evaluate(memory)).getBoolValue()){
                new Assignment(new LocalAtIdentifier(identifier.getIdentifierString()), MethodCall.getter(iterator, "next")).evaluate(memory);
                result = doBranch.evaluate(memory);
            }
        } catch (BreakStatement.Break e) {
            if (memory.getTopScope().containsTag(e.getTag())) {
                memory.popScope();
                return e.getResult();
            } else {
                throw e;
            }
        }
        memory.popScope();
        return result;
    }
}
