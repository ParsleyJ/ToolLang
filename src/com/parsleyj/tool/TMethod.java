package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TMethod extends TObject {

    protected String name;
    protected List<TClass> formalParametersTypes;

    public TMethod(String name, List<TClass> formalParametersTypes) {
        super(TBaseTypes.METHOD_CLASS);
        this.name = name;
        this.formalParametersTypes = formalParametersTypes;
    }

    public String getSimpleName() {
        return name;
    }

    public String getCompleteName() {
        String tmp = name + "(";
        for (int i = 0; i < getFormalParameterTypes().size(); ++i) {
            if (i != 0) tmp += ",";
            tmp += getFormalParameterTypes().get(i).getClassName();
        }
        tmp += ")";
        return tmp;
    }


    public List<TClass> getFormalParameterTypes() {
        return formalParametersTypes;
    }

    public TObject evaluate(TObject self, TObject... paramValues) {
        //TODO: implement
        return null;
    }

    public static String getCompleteNameFromActual(String name, TObject... params) {
        String tmp = name + "(";
        for (int i = 0; i < params.length; ++i) {
            if (i != 0) tmp += ",";
            tmp += params[i].getTClass().getClassName();
        }
        tmp += ")";
        return tmp;
    }

}
