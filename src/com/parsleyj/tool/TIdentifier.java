package com.parsleyj.tool;

/**
 * Created by Giuseppe on 19/05/15.
 */
public class TIdentifier extends TObject {

    public TIdentifier() {
        super(TBaseTypes.IDENTIFIER_CLASS);
    }

    public TIdentifier(String identifierString) {
        this();
        this.primitiveValue = identifierString;
    }

    public String getIdentifierString() {
        return (String) primitiveValue;
    }
}
