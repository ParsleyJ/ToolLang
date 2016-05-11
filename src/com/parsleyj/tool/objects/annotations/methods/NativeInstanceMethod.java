package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeInstanceMethod{
    String value();
    Visibility visibility() default Visibility.Public;
    String category() default ToolMethod.METHOD_CATEGORY_METHOD;
    ToolOperatorMethod.Mode mode() default ToolOperatorMethod.Mode.Binary;
}
