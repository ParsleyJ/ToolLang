package com.parsleyj.tool;

/**
 * Created by Giuseppe on 19/05/15.
 */
public class TField extends TObject {

    private TClass fieldType;
    private TIdentifier fieldName;

    @Override
    public TClass getTClass() {
        return TBaseTypes.FIELD_CLASS;
    }

    public TClass getFieldType() {
        return fieldType;
    }

    public void setFieldType(TClass fieldType) {
        this.fieldType = fieldType;
    }

    public TIdentifier getFieldName() {
        return fieldName;
    }

    public void setFieldName(TIdentifier fieldName) {
        this.fieldName = fieldName;
    }
}
