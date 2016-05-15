package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;

import java.util.ArrayList;
import java.util.Iterator;
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
    private RValue[] implicitArgumentExpressions;
    private RValue[] argumentExpressions;



    public MethodCall(String category, RValue callerExpression, String name, RValue[] implicitArgumentExpressions, RValue[] argumentExpressions) {
        this.category = category;
        this.callerExpression = callerExpression;
        this.name = name;
        this.implicitArgumentExpressions = implicitArgumentExpressions;
        this.argumentExpressions = argumentExpressions;
    }

    public static MethodCall method(RValue callerExpression, String name, RValue[] parameters) {
        return new MethodCall(
                ToolMethod.METHOD_CATEGORY_METHOD,
                callerExpression,
                name,
                new RValue[]{callerExpression},
                parameters
        );
    }

    public static MethodCall binaryOperator(RValue selfExpression, String operatorSym, RValue argExpression){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Binary, operatorSym),
                new RValue[]{selfExpression, argExpression},
                new RValue[]{});
    }

    public static MethodCall prefixOperator(String operatorSym, RValue selfExpression){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Prefix, operatorSym),
                new RValue[]{selfExpression},
                new RValue[]{});

    }

    public static MethodCall suffixOperator(RValue selfExpression, String operatorSym){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Suffix, operatorSym),
                new RValue[]{selfExpression},
                new RValue[]{});
    }

    public static MethodCall getter(RValue selfExpression, String name){
        return new MethodCall(
                ToolGetterMethod.METHOD_CATEGORY_GETTER,
                selfExpression,
                name,
                new RValue[]{selfExpression},
                new RValue[]{});
    }

    public static MethodCall setter(RValue selfExpression, String name, RValue argExpression){
        return new MethodCall(
                ToolSetterMethod.METHOD_CATEGORY_SETTER,
                selfExpression,
                name,
                new RValue[]{selfExpression, argExpression},
                new RValue[]{});
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
        return callMethod(memory, category, callerExpression, name, implicitArgumentExpressions, argumentExpressions);
    }

    private static ToolObject callMethod(Memory memory, String category, RValue callerExpression, String name, RValue[] implicitArgumentExpressions, RValue[] argumentExpressions) throws ToolNativeException {
        ToolObject caller = callerExpression.evaluate(memory);
        if(caller.isNull()) throw new CallOnNullException("Failed trying to call a method with null as caller object.");

        List<ToolObject> implicitArguments = new ArrayList<>();
        for (RValue iae : implicitArgumentExpressions) {
            implicitArguments.add(iae.evaluate(memory));
        }

        List<ToolObject> arguments = new ArrayList<>();
        for(RValue ae : argumentExpressions){
            arguments.add(ae.evaluate(memory));
        }

        List<ToolClass> argumentsTypes = arguments.stream().map(ToolObject::getBelongingClass).collect(Collectors.toList());

        MethodTable callableMethodTable = caller.generateCallableMethodTable();
        ToolMethod tm = callableMethodTable.resolve(caller, category, name, argumentsTypes);

        ToolObject result;
        memory.pushMethodCallFrame();

        for (int i = 0; i < tm.getImplicitArgumentNames().size(); ++i)
            memory.newLocalReference(tm.getImplicitArgumentNames().get(i), implicitArguments.get(i));

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
