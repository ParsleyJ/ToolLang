package com.parsleyj.tool;

/**
 * Created by Giuseppe on 17/05/15.
 */
public abstract class TLiteral extends TokenClass{

    private TClass tClass;

    public TLiteral(TClass tClass, String regex) {
        super(regex, "LITERAL");
        this.tClass = tClass;
    }

    public TClass getRepresentingIClass(){
        return tClass;
    }

    public abstract TObject convertLiteralToIObject(String literalInstance);
}
