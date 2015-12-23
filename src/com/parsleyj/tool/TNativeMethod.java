package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 19/05/15.
 */
public abstract class TNativeMethod extends TMethod {

    public static final TBlock NATIVE_METHOD_BLOCK = new TBlock(); //TODO: something to distinguish it from the normal blocks

    public TNativeMethod(String name, List<TClass> formalParametersTypes) {
        super(name, formalParametersTypes, NATIVE_METHOD_BLOCK);
        belongingClass = TBaseTypes.NATIVE_METHOD_CLASS;
    }

    @Override
    public TObject invoke(TObject self, TObject... paramValues) {
        TObject tmp = checkCallParametersCorrectness(paramValues);
        if (tmp != null) return tmp;

        return checkedInvoke(self, paramValues);
    }




    public abstract TObject checkedInvoke(TObject self, TObject... paramValues);
}
