package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolInternalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolMethodCall extends ToolObject implements RValue {
    private ToolObject instanceObjectCaller = null;
    private ToolClass staticClassCaller = null;
    private String name;
    private ToolObject[] arguments;

    private boolean isStatic;

    public ToolMethodCall(ToolClass staticClassCaller, String name, ToolObject[] arguments) {
        super(BaseTypes.C_METHOD_CALL);
        this.staticClassCaller = staticClassCaller;
        this.name = name;
        this.arguments = arguments;
        this.isStatic = true;
    }

    public ToolMethodCall(String name, ToolObject instanceObjectCaller, ToolObject[] arguments) {
        super(BaseTypes.C_METHOD_CALL);
        this.instanceObjectCaller = instanceObjectCaller;
        this.name = name;
        this.arguments = arguments;
        this.isStatic = false;
    }

    public ToolObject evaluate(Memory memory) throws ToolInternalException {
        List<ToolClass> argumentsTypes = new ArrayList<>();
        for (ToolObject argument : arguments) {
            argumentsTypes.add(argument.getBelongingClass());
        }

        ToolMethod tm;
        if (isStatic) {
            tm = staticClassCaller.findClassMethod(name, argumentsTypes);
        } else {
            tm = instanceObjectCaller.getBelongingClass().findInstanceMethod(name, argumentsTypes);
        }

        ToolObject result;
        if (tm == null) {
            StringBuilder sb = new StringBuilder("Method not found: <"+instanceObjectCaller.getBelongingClass().getClassName()+">."+name+"(");
            for (int i = 0; i < argumentsTypes.size(); i++) {
                ToolClass argumentType = argumentsTypes.get(i);
                sb.append(argumentType.getClassName());
                if(i < argumentsTypes.size()-1) sb.append(", ");
            }
            sb.append(")");
            throw new MethodNotFoundException(sb.toString());
        } else {
            memory.pushScope();
            if (!isStatic) {
                memory.newLocalReference("this", instanceObjectCaller); //TODO: use defined self/this keyword
            }
            for (int i = 0; i < tm.getArgumentNames().size(); ++i)
                memory.newLocalReference(tm.getArgumentNames().get(i), arguments[i]);

            result = tm.getBody().evaluate(memory);
            memory.createPhantomReference(result);
            memory.popScopeAndGC();
        }

        return result;
    }

    public static ToolObject executeScopedBlockWithNoParameters(RValue block, Memory memory) throws ToolInternalException {
        memory.pushScope();
        ToolObject result = block.evaluate(memory);
        memory.createPhantomReference(result);
        memory.popScopeAndGC();
        return result;
    }

}
