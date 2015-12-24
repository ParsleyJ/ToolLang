package com.parsleyj.tool.utils;

import com.parsleyj.tool.TBaseTypes;
import com.parsleyj.tool.TClass;
import com.parsleyj.tool.TObject;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 23/12/15.
 */
public class InternalUtils {

    public static TObject throwError(TClass errorClass, String description){
        TObject errorObject = errorClass.newInstance(newStringInstance(description));
        return TBaseTypes.THROWN_ERROR_CLASS.newInstance(errorObject);
    }

    private InternalUtils(){}

    public static TObject newStringInstance(String value) {
        if (value == null) value = "";
        return new TObject(TBaseTypes.STRING_CLASS, value){
            @Override
            public String getStringRepresentation() {
                return "\"" + (String) getPrimitiveValue() + "\"";
            }
        };
    }

    public static TObject newListInstance(ArrayList<TObject> list){
        return new TObject(TBaseTypes.LIST_CLASS, list);
    }
}
