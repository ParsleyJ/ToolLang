package com.parsleyj.tool.exceptions;

import com.parsleyj.tool.objects.BaseTypes;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
public class IndexOutOfBoundsExceptionTool extends ToolNativeException {
    public IndexOutOfBoundsExceptionTool(int index, int size) {
        super(BaseTypes.C_INDEX_OUT_OF_BOUNDS_EXCEPTION.newExceptionInstance("index: "+index+"; size: "+size));
    }
}
