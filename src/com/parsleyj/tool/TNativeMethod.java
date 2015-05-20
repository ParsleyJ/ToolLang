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
    public TObject evaluate(TObject self, TObject... paramValues) {
        TObject tmp = checkCallParametersCorrectness(paramValues);
        if (tmp != null) return tmp;

        return checkedInvoke(self, paramValues);
    }


    public TObject checkCallParametersCorrectness(TObject... actualParameters) {
        if (formalParametersTypes.size() == actualParameters.length)
            return TBaseTypes.INVALID_CALL_ERROR_CLASS.newInstance(TBaseTypes.newStringInstance(
                    "Failed attempt to force call '" + getCompleteName() + "' method providing an invalid number of parameters."));

        for (int i = 0; i < formalParametersTypes.size(); ++i) {
            if (!actualParameters[i].getTClass().isOrExtends(formalParametersTypes.get(i)))
                return TBaseTypes.INVALID_CALL_ERROR_CLASS.newInstance(TBaseTypes.newStringInstance(
                        "Failed attempt to force call '" + getCompleteName() + "' method providing an invalid parameter " +
                                "of type " + actualParameters[i].getTClass().getStringRepresentation() + " where " +
                                formalParametersTypes.get(i).getStringRepresentation() + " was expected"));
        }

        return null;
    }

    public abstract TObject checkedInvoke(TObject self, TObject... paramValues);
}
