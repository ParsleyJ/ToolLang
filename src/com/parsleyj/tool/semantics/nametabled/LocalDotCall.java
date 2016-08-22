package com.parsleyj.tool.semantics.nametabled;

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
public class LocalDotCall implements RValue{
    private String name;
    private RValue[] parameters;

    public LocalDotCall(String name, RValue[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        Memory.NameKind nameKind = memory.getTopScope().getNameTable().get(name);
        if(nameKind == null){
            return MethodCall.function(name, parameters).evaluate(memory); //throws a MethodNotFound
        }else switch (nameKind){
            case Variable:{
                throw new VisibilityException(memory, "Attempted to access private name via public access notation");
            }
            case Accessor:
            case VariableAndAccessor:{
                List<ToolObject> parameterObjectList = new ArrayList<>();
                for (RValue parameter : parameters) {
                    parameterObjectList.add(parameter.evaluate(memory));
                }
                //call () operator on object pointed by reference
                return MethodCall.binaryParametricOperator(
                        MethodCall.localGetter(name).evaluate(memory),
                        "(",
                        new ToolList(memory, parameterObjectList),
                        ")").evaluate(memory);
            }
            case Method:
                return MethodCall.function(name, parameters).evaluate(memory); //throws a MethodNotFound
            default:
                return null;
        }
    }


}
