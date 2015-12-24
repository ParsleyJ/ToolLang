package com.parsleyj.tool.interpreter;

import com.parsleyj.tool.TClass;
import com.parsleyj.tool.TObject;

/**
 * Created by Giuseppe on 17/05/15.
 */
public abstract class LiteralPattern extends TokenPattern {

    private TClass tClass;

    public LiteralPattern(TClass tClass, String regex) {
        super(regex, "LITERAL");
        this.tClass = tClass;
    }

    public TClass getRepresentingTClass() {
        return tClass;
    }

    public abstract TObject convertLiteralToTObject(String literalInstance);
}
