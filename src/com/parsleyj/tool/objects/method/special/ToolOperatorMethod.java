package com.parsleyj.tool.objects.method.special;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public class ToolOperatorMethod extends ToolMethod {

    public enum Mode{Prefix, Suffix, Binary, BinaryParametric}

    public static final String METHOD_CATEGORY_OPERATOR = "METHOD_CATEGORY_OPERATOR";


    public ToolOperatorMethod(Visibility visibility, Mode mode, ToolClass selfType, ToolClass argType, String operatorSym, RValue condition, RValue body) {
        super(METHOD_CATEGORY_OPERATOR,
                visibility,
                getOperatorMethodName(mode, operatorSym),
                (mode == Mode.Prefix || mode == Mode.Suffix)
                        ?
                        new FormalParameter[]{
                                new FormalParameter(Memory.SELF_IDENTIFIER, selfType),
                        }
                        :
                        new FormalParameter[]{
                                new FormalParameter(Memory.SELF_IDENTIFIER, selfType),
                                new FormalParameter(Memory.ARG_IDENTIFIER, argType),
                        },
                new FormalParameter[]{},
                condition,
                body);
    }

    public static String getOperatorMethodName(Mode mode, String operatorSym){
        return mode.name()+"Operator < "+operatorSym+" >";
    }
}
