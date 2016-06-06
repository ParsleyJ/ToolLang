package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;

import java.util.Objects;

/**
 * Created by Giuseppe on 06/06/16.
 * TODO: javadoc
 */
public class ToolNull extends ToolObject implements ToolType {
    public ToolNull(Memory m) {
        super(m, null);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getTypeName() throws ToolNativeException {
        return "Null";
    }

    @Override
    public boolean isOperator(ToolObject o) throws ToolNativeException {
        return Objects.equals(this.getId(), o.getId());
    }

    @Override
    public boolean canBeUsedAs(ToolType other) throws ToolNativeException {
        return false;
    }

    @Override
    public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
        return 0;
    }

    @Override
    public int getConvertibility(ToolType from) throws ToolNativeException {
        return 0;
    }
}
