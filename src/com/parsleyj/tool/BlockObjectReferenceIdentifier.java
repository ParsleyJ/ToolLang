package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.RValue;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BlockObjectReferenceIdentifier implements RValue {
    private String identifierString;

    public BlockObjectReferenceIdentifier(String identifierString) {
        this.identifierString = identifierString;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        ToolObject m = memory.getObjectByIdentifier(identifierString);
        if (m instanceof ToolBlock) {
            ((ToolBlock) m).setEvaluateAsObject(true);
        }
        return m;
    }

}
