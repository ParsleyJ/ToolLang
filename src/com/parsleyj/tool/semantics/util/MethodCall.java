package com.parsleyj.tool.semantics.util;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.special.ToolCtorMethod;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.utils.Triple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class MethodCall implements RValue {
    private String category;
    private RValue callerExpression;
    private String name;
    private RValue[] argumentExpressions;
    private boolean isScopedFunction = false;


    private MethodCall(String category, RValue callerExpression, String name, RValue[] argumentExpressions) {
        this.category = category;
        this.callerExpression = callerExpression;
        this.name = name;
        this.argumentExpressions = argumentExpressions;
    }

    public static MethodCall method(RValue callerExpression, String name, RValue[] parameters) {
        return new MethodCall(
                ToolMethod.METHOD_CATEGORY_METHOD,
                callerExpression,
                name,
                parameters
        );
    }

    public static MethodCall ctor(ToolObject newInstance,
                                  RValue[] parameters,
                                  MethodTable ctorTable){
        return new MethodCall(
                ToolCtorMethod.METHOD_CATEGORY_CONSTRUCTOR,
                newInstance,
                ToolCtorMethod.getCtorName(),
                parameters
        ){
            @Override
            public ToolObject evaluate(Memory memory) throws ToolNativeException {
                {
                    if(newInstance.isNull()) throw new CallOnNullException(memory,
                            "Failed trying to call a method with null as caller object.");

                    List<ToolObject> arguments = new ArrayList<>();
                    for(RValue ae : parameters){
                        arguments.add(ae.evaluate(memory));
                    }

                    ToolMethod tm = ctorTable.resolve(newInstance, ToolCtorMethod.METHOD_CATEGORY_CONSTRUCTOR,
                            ToolCtorMethod.getCtorName(), arguments);

                    ToolObject result;
                    memory.pushCallFrame(newInstance, new ArrayDeque<>());

                    for (int i = 0; i < tm.getArgumentNames().size(); ++i) {
                        memory.newLocalReference(tm.getArgumentNames().get(i), arguments.get(i));
                        memory.getTopScope().getNameTable().put(tm.getArgumentNames().get(i), Memory.NameKind.Variable);
                    }

                    try {
                        result = tm.getBody().evaluate(memory);
                    }catch (ToolNativeException tne) {
                        tne.addFrameToTrace("\tat: "+ tm.completeInstanceMethodName(newInstance.getBelongingClass()));
                        memory.returnFromCallError();
                        throw tne;
                    }
                    memory.returnFromCall();
                    return result;
                }
            }
        };
    }

    public static MethodCall binaryOperator(RValue selfExpression, String operatorSym, RValue argExpression){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Binary, operatorSym),
                new RValue[]{argExpression});
    }

    public static MethodCall binaryOperatorReverse(RValue argExpression, String operatorSym, RValue selfExpression){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Binary, operatorSym),
                new RValue[]{argExpression});
    }

    public static MethodCall prefixOperator(String operatorSym, RValue selfExpression){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Prefix, operatorSym),
                new RValue[]{});

    }

    public static MethodCall suffixOperator(RValue selfExpression, String operatorSym){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Suffix, operatorSym),
                new RValue[]{});
    }

    public static MethodCall binaryParametricOperator(RValue selfExpression,
                                                      String operatorSym1, RValue argExpression, String operatorSym2){
        return new MethodCall(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                selfExpression,
                ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.BinaryParametric,
                        operatorSym1+operatorSym2),
                new RValue[]{argExpression}
        );
    }


    public static MethodCall getter(RValue selfExpression, String name){
        return new MethodCall(
                ToolGetterMethod.METHOD_CATEGORY_GETTER,
                selfExpression,
                name,
                new RValue[]{});
    }

    public static MethodCall setter(RValue selfExpression, String name, RValue argExpression){
        return new MethodCall(
                ToolSetterMethod.METHOD_CATEGORY_SETTER,
                selfExpression,
                name,
                new RValue[]{argExpression});
    }

    public static MethodCall function(String name, RValue[] parameters){
        MethodCall mc = new MethodCall(
                ToolMethod.METHOD_CATEGORY_METHOD,
                null,
                name,
                parameters
        );
        mc.isScopedFunction = true;
        return mc;
    }

    public static MethodCall localGetter(String name){
        MethodCall mc = new MethodCall(
                ToolGetterMethod.METHOD_CATEGORY_GETTER,
                null,
                name,
                new RValue[]{}
        );
        mc.isScopedFunction = true;
        return mc;
    }

    public static MethodCall localSetter(String name, RValue argExpression){
        MethodCall mc = new MethodCall(
                ToolSetterMethod.METHOD_CATEGORY_SETTER,
                null,
                name,
                new RValue[]{argExpression}
        );
        mc.isScopedFunction = true;
        return mc;
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
        return isScopedFunction ?
                callFunction(memory, category, name, argumentExpressions)
                :
                callMethod(memory, category, callerExpression, name, argumentExpressions);
    }

    private static ToolObject callMethod(Memory memory,
                                         String category,
                                         RValue callerExpression,
                                         String name,
                                         RValue[] argumentExpressions) throws ToolNativeException {
        ToolObject caller = callerExpression.evaluate(memory);
        if(caller.isNull()) throw new CallOnNullException(memory,
                "Failed trying to call a method with null as caller object.");

        List<ToolObject> arguments = new ArrayList<>();
        for(RValue ae : argumentExpressions){
            arguments.add(ae.evaluate(memory));
        }

        MethodTable callableMethodTable = caller.generateCallableMethodTable();
        ToolMethod tm = callableMethodTable.resolve(caller, category, name, arguments);

        return tm.call(memory, caller, arguments, new ArrayDeque<>());
    }

    private static ToolObject callFunction(Memory memory,
                                           String category,
                                           String name,
                                           RValue[] argumentExpressions) throws ToolNativeException{
        List<ToolObject> arguments = new ArrayList<>();
        for(RValue ae : argumentExpressions){
            arguments.add(ae.evaluate(memory));
        }

        Triple<ArrayDeque<Memory.Scope>, ToolMethod, ToolObject> tml =memory.resolveFunction(category, name, arguments);
        ArrayDeque<Memory.Scope> definitionScope = tml.getFirst();
        ToolMethod tm = tml.getSecond();
        ToolObject caller = tml.getThird();

        return tm.call(memory, caller, arguments, definitionScope);
    }

    public static ToolObject executeScopedBlockWithNoParameters(RValue block, Memory memory) throws ToolNativeException{
        memory.pushScope();
        ToolObject result = block.evaluate(memory);
        memory.popScope();
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
