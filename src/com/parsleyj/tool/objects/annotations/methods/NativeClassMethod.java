package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeClassMethod {
    Visibility value() default Visibility.Public;
    String category() default ToolMethod.METHOD_CATEGORY_METHOD;
}
