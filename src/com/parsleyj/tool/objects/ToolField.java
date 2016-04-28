package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolField extends ToolObject {
    private ToolClass fieldType;
    private String identifier;

    public ToolField(ToolClass fieldType, String identifier) {
        super(BaseTypes.C_FIELD);
        this.fieldType = fieldType;
        this.identifier = identifier;
    }

    public ToolClass getFieldType() {
        return fieldType;
    }

    public String getIdentifier() {
        return identifier;
    }
}
