package com.parsleyj.tool;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BinaryOperationMethodCall extends MethodCall {

    private final RValue i1;
    private final String methodName;
    private final RValue i2;

    public BinaryOperationMethodCall(RValue i1, String methodName, RValue i2) {
        super(i1, methodName, new RValue[]{i2});
        this.i1 = i1;
        this.methodName = methodName;
        this.i2 = i2;
    }

    @Override
    public String toString() {
        return i1 + "." + methodName + "(" + i2 + ")";
    }
}
