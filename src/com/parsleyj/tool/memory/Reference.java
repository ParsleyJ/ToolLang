package com.parsleyj.tool.memory;

import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolClass;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class Reference {

    private ToolClass referenceType;
    private String identifierString;
    private ToolObject value;


    public Reference(String identifierString, ToolClass type, ToolObject value) {
        this.referenceType = type;
        this.identifierString = identifierString;
        this.value = value;
    }

    public Reference(String identifierString, ToolObject obj) {
        this.referenceType = obj.getBelongingClass();
        this.identifierString = identifierString;
        this.value = obj;
    }


    public String getIdentifierString() {
        return identifierString;
    }

    public ToolClass getReferenceType() {
        return referenceType;
    }

    public ToolObject getValue() {
        return value;
    }

    public void setValue(ToolObject value) {
        this.value = value;
    }

    //TODO: add type check
}
