package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.objects.method.Visibility;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public @interface CtorMethod {
    Visibility value() default Visibility.Public;
}
