package com.parsleyj.tool.semantics;

import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class PrefixUnaryOperationMethodCall extends MethodCall {

    public PrefixUnaryOperationMethodCall(String methodName, RValue i1) {
        super(ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, i1, methodName, new RValue[]{i1}, new RValue[]{});
    }

}
