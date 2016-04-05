package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class NewVarDeclaration implements LValue {

    private String identifierString;

    public NewVarDeclaration(String identifierString) {
        this.identifierString = identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolInternalException {
        m.newLocalReference(identifierString, o);
    }

    @Override
    public ToolObject evaluate(Memory memory) {
        return BaseTypes.O_NULL;
    }

    @Override
    public String toString() {
        return "." + identifierString;
    }
}
