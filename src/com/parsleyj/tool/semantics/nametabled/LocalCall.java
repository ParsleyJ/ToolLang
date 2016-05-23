package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 20/05/16.
 * TODO: javadoc
 */
public class LocalCall implements RValue {
    private String name;
    private RValue[] parameters;

    public LocalCall(String name, RValue[] parameters) {
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
                List<ToolObject> parameterObjectList = new ArrayList<>();
                for (RValue parameter : parameters) {
                    parameterObjectList.add(parameter.evaluate(memory));
                }
                //call () operator on object pointed by reference
                return MethodCall.binaryParametricOperator(
                        memory.getObjectByIdentifier(name),
                        "(",
                        new ToolList(parameterObjectList),
                        ")").evaluate(memory);
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
                        new ToolList(parameterObjectList),
                        ")").evaluate(memory);
            }
            case Method:
                return MethodCall.function(name, parameters).evaluate(memory); //throws a MethodNotFound
            default:
                return null;
        }
    }
}
