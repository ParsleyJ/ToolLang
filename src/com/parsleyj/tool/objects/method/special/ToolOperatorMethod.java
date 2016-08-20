package com.parsleyj.tool.objects.method.special;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolOperatorMethod {

    public enum Mode{Prefix, Suffix, Binary, Ternary, BinaryParametric}

    public static final String METHOD_CATEGORY_OPERATOR = "METHOD_CATEGORY_OPERATOR";

    public static String getOperatorMethodName(Mode mode, String operatorSym){
        return mode.name()+"Operator < "+operatorSym+" >";
    }
}
