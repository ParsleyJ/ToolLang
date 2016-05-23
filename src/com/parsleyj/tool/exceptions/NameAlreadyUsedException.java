package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.exception.ToolException;

/**
 * Created by Giuseppe on 20/05/16.
 * TODO: javadoc
 */
public class NameAlreadyUsedException extends ToolNativeException {
    public NameAlreadyUsedException(String msg) {
        super(BaseTypes.C_NAME_ALREADY_USED_EXCEPTION.newExceptionInstance(msg));
    }
}
