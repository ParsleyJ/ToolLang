package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class InvalidIndexListException extends ToolNativeException {

    public InvalidIndexListException(String msg) {
        super(BaseTypes.C_INVALID_INDEX_LIST_EXCEPTION.newExceptionInstance(msg));
    }
}
