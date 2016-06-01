package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.NamedLValue;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 27/05/16.
 * TODO: javadoc
 */
public class DefinitionPropertyVal implements NamedLValue {

    private String name;

    public DefinitionPropertyVal(String name) {
        this.name = name;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        throw new InvalidDefinitionException(memory, "Val property declaration cannot be used as r-value");
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        new DefinitionVariable(name).assign(o, m);
        new DefinitionGetter(name, memory1 -> memory1.getObjectByIdentifier(name)).evaluate(m);
    }

    @Override
    public String getIdentifierString() {
        return name;
    }
}
