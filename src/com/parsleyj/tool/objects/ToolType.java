package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 01/06/16.
 * TODO: javadoc
 */
public interface ToolType {
    @NativeInstanceMethod(value = "typeName", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    String getTypeName() throws ToolNativeException;

    @NativeInstanceMethod(value = "is", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Binary)
    boolean isOperator (ToolObject o) throws ToolNativeException;

    @NativeInstanceMethod
    boolean canBeUsedAs(ToolType other) throws ToolNativeException;

    @NativeInstanceMethod
    int getObjectConvertibility(ToolObject from) throws ToolNativeException;

    @NativeInstanceMethod
    int getConvertibility(ToolType from) throws ToolNativeException;
}
