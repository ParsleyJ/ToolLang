package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 13/04/16.
 * TODO: javadoc
 */
public class ToolArithmeticException extends ToolInternalException {
    public ToolArithmeticException(String msg) {
        super(BaseTypes.C_ARITHMETIC_EXCEPTION.newExceptionInstance(msg));
    }
}
