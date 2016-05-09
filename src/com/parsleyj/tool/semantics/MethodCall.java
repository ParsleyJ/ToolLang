package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.classes.ToolClass;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.ToolObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class MethodCall implements RValue {
    private String category;
    private RValue callerExpression;
    private String name;
    private RValue[] argumentExpressions;



    public MethodCall(String category, RValue callerExpression, String name, RValue[] argumentExpressions) {
        this.category = category;
        this.callerExpression = callerExpression;
        this.name = name;
        this.argumentExpressions = argumentExpressions;
    }

    public MethodCall(String category, RValue callerExpression, MethodCall methodCall){
        this.category = category;
        this.callerExpression = callerExpression;
        this.name = methodCall.getName();
        this.argumentExpressions = methodCall.getArgumentExpressions();
    }

    public MethodCall(String category, DotNotationField dotNotationField, RValue[] argumentExpressions){
        this.category = category;
        this.callerExpression = dotNotationField.getUnevaluatedExpression();
        this.name = dotNotationField.getIdentifier().getIdentifierString();
        this.argumentExpressions = argumentExpressions;
    }

    public RValue getCallerExpression() {
        return callerExpression;
    }

    public String getName() {
        return name;
    }

    public RValue[] getArgumentExpressions() {
        return argumentExpressions;
    }

    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject caller = callerExpression.evaluate(memory);
        if(caller.isNull()) throw new CallOnNullException("Failed trying to call a method with null as caller object.");

        List<ToolObject> arguments = new ArrayList<>();
        for(RValue ae : argumentExpressions){
            arguments.add(ae.evaluate(memory));
        }

        List<ToolClass> argumentsTypes = arguments.stream().map(ToolObject::getBelongingClass).collect(Collectors.toList());

        ToolMethod tm = caller.generateCallableMethodTable().resolve(category, name, argumentsTypes);
        if (tm == null) {
            StringBuilder sb = new StringBuilder("Method not found: <"+ caller.getBelongingClass().getClassName()+">."+name+"(");
            for (int i = 0; i < argumentsTypes.size(); i++) {
                ToolClass argumentType = argumentsTypes.get(i);
                sb.append(argumentType.getClassName());
                if(i < argumentsTypes.size()-1) sb.append(", ");
            }
            sb.append(")");
            throw new MethodNotFoundException(sb.toString());
        }


        ToolObject result;
        memory.pushMethodCallFrame(caller);
        for (int i = 0; i < tm.getArgumentNames().size(); ++i)
            memory.newLocalReference(tm.getArgumentNames().get(i), arguments.get(i));

        result = tm.getBody().evaluate(memory);
        memory.createPhantomReference(result);
        memory.popScopeAndGC();


        return result;
    }

    public static ToolObject executeScopedBlockWithNoParameters(RValue block, Memory memory) throws ToolNativeException {
        memory.pushScope();
        ToolObject result = block.evaluate(memory);
        memory.createPhantomReference(result);
        memory.popScopeAndGC();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(callerExpression+"."+name+"(");
        for (int i = 0; i < argumentExpressions.length; i++) {
            RValue argumentType = argumentExpressions[i];
            sb.append(argumentType);
            if(i < argumentExpressions.length-1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}
