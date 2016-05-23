package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class NameNotFoundException extends ToolNativeException {
    public NameNotFoundException(String msg){
        super(BaseTypes.C_NAME_NOT_FOUND_EXCEPTION.newExceptionInstance(msg));
    }
}
