package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 19/05/15.
 */
public abstract class TNativeMethod extends TMethod {


    public TNativeMethod(String name, List<TClass> formalParametersTypes) {
        super(name, formalParametersTypes);
        belongingClass = TBaseTypes.NATIVE_METHOD_CLASS;
    }

    @Override
    public abstract TObject evaluate(TObject self, TObject... paramValues);
}
