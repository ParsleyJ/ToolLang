package com.parsleyj.tool;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class PrefixUnaryOperationMethodCall extends MethodCall {

    public PrefixUnaryOperationMethodCall(String methodName, RValue i1) {
        super(i1, methodName, new RValue[]{});
    }

}
