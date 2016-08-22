package com.parsleyj.tool.semantics.expr;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public class TaggedExpression implements RValue {

    private ArrayList<String> tags = new ArrayList<>();
    private RValue subExpression;
    private boolean consumed = false;

    public TaggedExpression(Identifier identifier, RValue subExpression) {
        this.tags.add(identifier.getIdentifierString());
        this.subExpression = subExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        if (!consumed) {
            tags.forEach(t -> subExpression.addTag(t));
            consumed = true;
        }
        return subExpression.evaluate(memory);
    }

    @Override
    public void addTag(String s) {
        tags.add(s);
    }
}
