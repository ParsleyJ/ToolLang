package com.parsleyj.tool;

import com.parsleyj.tool.utils.InternalUtils;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TMethod extends TObject {

    protected String name;
    protected List<TClass> formalParametersTypes;
    protected TBlock bodyBlock;

    public TMethod(String name, List<TClass> formalParametersTypes, TBlock body) {
        super(TBaseTypes.METHOD_CLASS);
        this.name = name;
        this.formalParametersTypes = formalParametersTypes;
        this.bodyBlock = body;
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

    public TObject invoke(TObject self, TObject... paramValues) {

        TObject tmp = checkCallParametersCorrectness(paramValues);
        if (tmp != null) return tmp;

        //a stack is added in namespace, containing the arguments
        self.getNamespace().pushNewScope();
        //TODO: add parameters in stack

        //another stack is automatically added for the block execution
        TObject result = bodyBlock.evaluate(self.getNamespace(), TBaseTypes.DEFAULT_INTERPRETER_OBJECT);

        self.getNamespace().popScope();

        return result;
    }

    public TObject checkCallParametersCorrectness(TObject... actualParameters) {
        if (formalParametersTypes.size() == actualParameters.length)
            return InternalUtils.throwError(TBaseTypes.INVALID_CALL_ERROR_CLASS,
                    "Failed attempt to invoke '" + getCompleteName() + "' method providing an invalid number of parameters.");

        for (int i = 0; i < formalParametersTypes.size(); ++i) {
            if (!actualParameters[i].getTClass().isOrExtends(formalParametersTypes.get(i)))
                return InternalUtils.throwError(TBaseTypes.INVALID_CALL_ERROR_CLASS,
                        "Failed attempt to invoke '" + getCompleteName() + "' method providing an invalid parameter " +
                                "of type " + actualParameters[i].getTClass().getStringRepresentation() + " where " +
                                formalParametersTypes.get(i).getStringRepresentation() + " was expected.");
        }

        return null; //return null if everything is ok
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
