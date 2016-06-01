package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public class TaggedExpression implements RValue {

    private Identifier identifier;
    private RValue subExpression;

    public TaggedExpression(Identifier identifier, RValue subExpression) {
        this.identifier = identifier;
        this.subExpression = subExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        subExpression.addTag(identifier.getIdentifierString());
        return subExpression.evaluate(memory);
    }
}
