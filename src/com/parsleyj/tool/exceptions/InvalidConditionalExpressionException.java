package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.BaseTypes;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class InvalidConditionalExpressionException extends ToolInternalException {
    public InvalidConditionalExpressionException(String msg) {
        super(BaseTypes.C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION.newExceptionInstance(msg));
    }
}