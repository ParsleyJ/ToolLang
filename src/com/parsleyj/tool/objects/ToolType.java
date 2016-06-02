package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public interface ToolType {
    String getTypeName() throws ToolNativeException;
    boolean isOperator (ToolObject o) throws ToolNativeException;
    boolean canBeUsedAs(ToolType other) throws ToolNativeException;
    int getObjectConvertibility(ToolObject from) throws ToolNativeException;
    int getConvertibility(ToolType from) throws ToolNativeException;
}
