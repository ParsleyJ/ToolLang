package com.parsleyj.tool.semantics;

import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BinaryOperationMethodCall extends MethodCall {

    public BinaryOperationMethodCall(RValue i1, String methodName, RValue i2) {
        super(ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, i1, methodName, new RValue[]{i2});
    }

}
