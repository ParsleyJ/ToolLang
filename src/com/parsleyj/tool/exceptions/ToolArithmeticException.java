package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class ToolArithmeticException extends ToolNativeException {
    public ToolArithmeticException(String msg) {
        super(BaseTypes.C_ARITHMETIC_EXCEPTION.newExceptionInstance(msg));
    }
}
