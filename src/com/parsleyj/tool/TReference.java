package com.parsleyj.tool;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TReference extends TObject {

    private TIdentifier identifier;
    private Integer referencedId;

    public TReference(TIdentifier identifier, Integer objid) {
        super(TBaseTypes.REFERENCE_CLASS);
        this.identifier = identifier;
        this.referencedId = objid;
    }

    public TIdentifier getTIdentifier() {
        return identifier;
    }

    public Integer getReferencedId() {
        return referencedId;
    }
}
