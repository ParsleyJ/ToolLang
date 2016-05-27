package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.exceptions.VisibilityException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 19/05/16.
 * TODO: javadoc
 */
public class ObjectDotCall implements RValue {

    private RValue callerExpression;
    private String name;
    private RValue[] parameters;

    public ObjectDotCall(RValue callerExpression, String name, RValue[] parameters) {
        this.callerExpression = callerExpression;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject caller = callerExpression.evaluate(memory);
        if(caller == null || caller.isNull()) throw new CallOnNullException(memory, "Failed trying to call a method with null as caller object.");

        Memory.NameKind nameKind = caller.getMembersScope().getNameTable().get(name);
        if(nameKind == null){
            //tries to call (throws error)
            return MethodCall.method(caller, name, parameters).evaluate(memory);
        }else switch (nameKind){
            case Variable:
                throw new VisibilityException(memory, "Attempted to access private name via public access notation");
            case Accessor:
            case VariableAndAccessor: {
                List<ToolObject> parameterObjectList = new ArrayList<>();
                for (RValue parameter : parameters) {
                    parameterObjectList.add(parameter.evaluate(memory));
                }
                //call () operator on object returned by getter
                return MethodCall.binaryParametricOperator(
                        MethodCall.getter(caller, name).evaluate(memory),
                        "(",
                        new ToolList(memory, parameterObjectList),
                        ")").evaluate(memory);

            }
            case Method:
                //classic method call
                return MethodCall.method(caller, name, parameters).evaluate(memory);
            default:
                return null;
        }
    }
}
