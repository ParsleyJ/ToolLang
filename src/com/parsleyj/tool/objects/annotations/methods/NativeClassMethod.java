package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.objects.Visibility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeClassMethod {
    Visibility value() default Visibility.Public;
}
