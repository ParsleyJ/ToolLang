package com.parsleyj.tool;

/**
 * Created by Giuseppe on 17/05/15.
 */
public abstract class TLiteral extends TokenClass { //TODO: is TLiteral a TObject?

    private TClass tClass;

    public TLiteral(TClass tClass, String regex) {
        super(regex, "LITERAL");
        this.tClass = tClass;
    }

    public TClass getRepresentingTClass() {
        return tClass;
    }

    public abstract TObject convertLiteralToTObject(String literalInstance);
}
