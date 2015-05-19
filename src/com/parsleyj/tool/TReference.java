package com.parsleyj.tool;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TReference {

    //TODO:

    private ToolMemory defaultMemory;

    private String refId;

    public TClass getType() {
        return defaultMemory.getReferencedObject(refId).getTClass();
    }

}
