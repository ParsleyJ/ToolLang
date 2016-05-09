package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.memory.Memory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Giuseppe on 02/05/16.
 * TODO: javadoc
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplicitParameter {
    String value() default Memory.SELF_IDENTIFIER;
}
