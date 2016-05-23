package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.NameAlreadyUsedException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class DefinitionSetter implements RValue {

    private String identifierString;
    private ToolClass argType;
    private RValue body;

    public DefinitionSetter(String identifierString, ToolClass argType, RValue body) {
        this.argType = argType;
        this.body = body;
        this.identifierString = identifierString;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        Memory.NameKind nk = memory.getTopScope().getNameTable().get(identifierString);
        if (nk==null){
            memory.getTopScope().getNameTable().put(identifierString, Memory.NameKind.Accessor);
            return createAndAddSetter(memory, identifierString, argType, body);
        }else switch (nk){
            case Variable:
                memory.getTopScope().getNameTable().put(identifierString, Memory.NameKind.VariableAndAccessor);
                return createAndAddSetter(memory, identifierString, argType, body);
            case Accessor:
            case VariableAndAccessor:
                return  createAndAddSetter(memory, identifierString, argType, body);
            case Method:
                throw new NameAlreadyUsedException("Cannot define setter: '"+identifierString+"' is an already used name in this scope.");
            default:
                return null;
        }
    }

    public static ToolMethod createAndAddSetter(Memory memory, String name, ToolClass argType, RValue body) throws ToolNativeException{
        ToolMethod method = new ToolMethod(
                ToolSetterMethod.METHOD_CATEGORY_SETTER,
                Visibility.Public,
                name,
                new FormalParameter[]{
                        new FormalParameter("arg", argType)
                },
                new FormalParameter[]{},
                new ToolBoolean(true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }
}
