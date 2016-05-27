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
    private Integer id;


    public Reference(String identifierString, ToolClass type, Integer id) {
        this.referenceType = type;
        this.identifierString = identifierString;
        this.id = id;
    }

    public Reference(String identifierString, ToolObject obj) {
        this.referenceType = obj.getBelongingClass();
        this.identifierString = identifierString;
        this.id = obj.getId();
    }


    public String getIdentifierString() {
        return identifierString;
    }

    public Integer getPointedId() {
        return id;
    }

    public void setPointedId(Integer id) {
        this.id = id;
    }

    public ToolClass getReferenceType() {
        return referenceType;
    }

    //TODO: add type check
}
