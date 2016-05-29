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
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject owner = callerExpression.evaluate(memory);
        if(owner == null || owner.isNull()) throw new CallOnNullException(memory, "Failed trying to call a method with null as owner object.");
        if(!memory.protectedAccessTo(owner)) throw new VisibilityException(memory, "There is not private access from this context to "+owner+" .");
        Memory.NameKind nameKind = owner.getMembersScope().getNameTable().get(name);
        if(nameKind == null){
            //tries to call (throws error)
            return MethodCall.method(owner, name, parameters).evaluate(memory);
        }else switch (nameKind){
            case Variable:
            case VariableAndAccessor:{
                List<ToolObject> parameterObjectList = new ArrayList<>();
                for (RValue parameter : parameters) {
                    parameterObjectList.add(parameter.evaluate(memory));
                }
                //call () operator on object pointed by reference
                return MethodCall.binaryParametricOperator(
                        owner.getReferenceMember(name).getValue(),
                        "(",
                        new ToolList(memory, parameterObjectList),
                        ")").evaluate(memory);
            }
            case Accessor: {
                throw new VisibilityException(memory, "Attempted to access public name via private access notation");
            }
            case Method:
                //classic method call
                return MethodCall.method(owner, name, parameters).evaluate(memory);
            default:
                return null;
        }
    }
}
