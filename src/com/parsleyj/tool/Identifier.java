package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class Identifier implements LValue {
    private String identifierString;

    public Identifier(String identifierString) {
        this.identifierString = identifierString;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        return memory.getObjectByIdentifier(identifierString);
    }

    public String getIdentifierString() {
        return identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolInternalException {
        m.updateReference(identifierString, o);
    }

    @Override
    public String toString() {
        return identifierString;
    }
}
