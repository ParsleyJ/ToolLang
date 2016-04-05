package com.parsleyj.tool;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BinaryOperationMethodCall extends ToolMethodCall {

    private final ToolObject i1;
    private final String methodName;
    private final ToolObject i2;

    public BinaryOperationMethodCall(ToolObject i1, String methodName, ToolObject i2) {
        super(methodName, i1, new ToolObject[]{i2});
        this.i1 = i1;
        this.methodName = methodName;
        this.i2 = i2;
    }

    @Override
    public String toString() {
        return i1 + "." + methodName + "(" + i2 + ")";
    }
}
