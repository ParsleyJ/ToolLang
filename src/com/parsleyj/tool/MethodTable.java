package com.parsleyj.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class MethodTable {
    private List<ToolMethod> methods = new ArrayList<>();

    public void add(ToolMethod method) {
        //todo: THIS IS TEMPORARY, must check ambiguous methods
        methods.add(0, method);
    }

    public ToolMethod resolve(String name, List<ToolClass> argumentTypes) {
        //TODO: THIS IS TEMPORARY, implement correct function call resolution
        //todo step1: get all candidates (correct names, visible from call point(?))
        /*todo step2: select viable functions among candidates.
          todo      a viable function is a function that has types and number of arguments
          todo      compatible with the the call. */
        //todo      Compatible means it can be converted (following some specific rules) in the other type
        //todo step3: choose the best function among the viables. cases:
        //todo              1) there are no viable functions: throw MethodNotFoundException
        //todo              2) there is 1 viable function: that's the one!
        //todo              3) there are more than 1 functions: a rank system must be used and:
        //todo                  3.1) there is one method at first place: that's the one!
        //todo                  3.2) there are more than one method at first place: throw AmbiguousMethodCallException
        for (ToolMethod method : methods) {
            boolean found = true;
            List<ToolClass> methodArgumentTypes = method.getArgumentTypes();
            if (method.getName().equals(name) && (methodArgumentTypes.size() == argumentTypes.size())) {
                for (int i = 0; i < argumentTypes.size(); i++) {
                    ToolClass argumentType = argumentTypes.get(i);
                    ToolClass methodArgumentType = methodArgumentTypes.get(i);

                    Integer argumentTypeID1 = argumentType.getId();
                    Integer argumentTypeID2 = methodArgumentType.getId();
                    if (!Objects.equals(argumentTypeID1, argumentTypeID2) ){
                        found = false;
                        break;
                    }
                }
            } else {
                found = false;
            }
            if (found) return method;
        }
        return null;
    }
}
