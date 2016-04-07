package com.parsleyj.tool;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class Reference extends ToolObject {

    private String identifierString;
    private Integer id;

    public Reference(String identifierString) {
        this.identifierString = identifierString;
        this.id = BaseTypes.O_NULL.getId();
    }

    public Reference(String identifierString, Integer id) {
        super(BaseTypes.C_REFERENCE);
        this.identifierString = identifierString;
        this.id = id;
    }

    public Reference(String identifierString, ToolObject obj) {
        super(BaseTypes.C_REFERENCE);
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


    @Override
    public String toString() {
        return "<REF:"+identifierString+":"+id+">";
    }
}
