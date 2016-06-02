package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public interface ToolType {
    String getTypeName();
    boolean isOperator (ToolObject o);
    boolean canBeUsedAs(ToolType other);
    int getConvertibility(ToolObject from);
    int getConvertibility(ToolType from);
}
