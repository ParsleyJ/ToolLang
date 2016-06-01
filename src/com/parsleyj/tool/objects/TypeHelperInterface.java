package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public interface TypeHelperInterface {
    boolean is (ToolObject o);
    boolean canBeUsedAs(TypeHelperInterface other);
    int getConvertibility(TypeHelperInterface from);
}
