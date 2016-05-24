package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;

/**
 * Created by Giuseppe on 22/05/16.
 * TODO: javadoc
 */
public class LocalAtIdentifier implements LValue{
    private String identifierString;

    public LocalAtIdentifier(String identifierString) {
        this.identifierString = identifierString;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        return memory.getObjectByIdentifier(identifierString);
    }

    public String getIdentifierString() {
        return identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        m.updateReference(identifierString, o);
    }

    @Override
    public String toString() {
        return "@"+identifierString;
    }



}