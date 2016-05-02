package com.parsleyj.tool.semantics;

import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;

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
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
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
