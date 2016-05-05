package com.parsleyj.tool.objects.annotations.methods;

import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

/**
 * Created by Giuseppe on 05/05/16.
 * TODO: javadoc
 */
public @interface OperatorMethod {
    Visibility value() default Visibility.Public;
    ToolOperatorMethod.Mode mode() default ToolOperatorMethod.Mode.Binary;
    String[] syntaxStructure();
}
