package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolField extends ToolObject {
    private ToolClass fieldType;
    private String identifier;
    private ToolObject defaultValue;

    public ToolField(ToolClass fieldType, String identifier, ToolObject defaultValue) {
        super(BaseTypes.C_FIELD);
        this.fieldType = fieldType;
        this.identifier = identifier;
        this.defaultValue = defaultValue;
    }

    public ToolClass getFieldType() {
        return fieldType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ToolObject getDefaultValue() {
        return defaultValue;
    }
}
