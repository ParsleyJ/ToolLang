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
        //TODO: THIS IS TEMPORARY, implement substitution principle
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
