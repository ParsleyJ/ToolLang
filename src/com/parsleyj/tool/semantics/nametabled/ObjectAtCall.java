package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.exceptions.VisibilityException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.util.MethodCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class ObjectAtCall implements RValue {

    private RValue callerExpression;
    private String name;
    private RValue[] parameters;

    public ObjectAtCall(RValue callerExpression, String name, RValue[] parameters) {
        this.callerExpression = callerExpression;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException { //TODO check private access
        ToolObject caller = callerExpression.evaluate(memory);
        if(caller == null || caller.isNull()) throw new CallOnNullException("Failed trying to call a method with null as caller object.");

        Memory.NameKind nameKind = caller.getMembersScope().getNameTable().get(name);
        if(nameKind == null){
            //tries to call (throws error)
            return MethodCall.method(caller, name, parameters).evaluate(memory);
        }else switch (nameKind){
            case Variable:
            case VariableAndAccessor:{
                List<ToolObject> parameterObjectList = new ArrayList<>();
                for (RValue parameter : parameters) {
                    parameterObjectList.add(parameter.evaluate(memory));
                }
                //call () operator on object pointed by reference
                return MethodCall.binaryParametricOperator(
                        memory.getObjectById(caller.getReferenceMember(name).getPointedId()),
                        "(",
                        new ToolList(parameterObjectList),
                        ")").evaluate(memory);
            }
            case Accessor: {
                throw new VisibilityException("Attempted to access public name via private access notation");
            }
            case Method:
                //classic method call
                return MethodCall.method(caller, name, parameters).evaluate(memory);
            default:
                return null;
        }
    }
}
