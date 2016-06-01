package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.NamedLValue;

/**
 * Created by Giuseppe on 27/05/16.
 * TODO: javadoc
 */
public class DefinitionPropertyVar implements NamedLValue {

    private String name;

    public DefinitionPropertyVar(String name) {
        this.name = name;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        new DefinitionVariable(name).assign(o, m);
        new DefinitionGetter(name, memory -> new LocalAtIdentifier(name).evaluate(memory)).evaluate(m);
        new DefinitionSetter(name, o.getBelongingClass(), memory -> {
            ToolObject arg = memory.getObjectByIdentifier(Memory.ARG_IDENTIFIER);
            new LocalAtIdentifier(name).assign(arg, memory);
            return arg;
        }).evaluate(m);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        throw new InvalidDefinitionException(memory, "Var property declaration cannot be used as r-value");
    }

    @Override
    public String getIdentifierString() {
        return name;
    }
}
