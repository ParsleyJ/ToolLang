package com.parsleyj.tool.memory;

import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class Reference {

    private String identifierString;
    private Integer id;

    public Reference(String identifierString) {
        this.identifierString = identifierString;
        this.id = BaseTypes.O_NULL.getId();
    }

    public Reference(String identifierString, Integer id) {
        this.identifierString = identifierString;
        this.id = id;
    }

    public Reference(String identifierString, ToolObject obj) {
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


}
