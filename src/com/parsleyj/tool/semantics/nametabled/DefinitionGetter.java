package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.NameAlreadyUsedException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class DefinitionGetter implements RValue{
    private String identifierString;
    private RValue body;

    public DefinitionGetter(String identifierString, RValue body) {
        this.identifierString = identifierString;
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        Memory.NameKind nk = memory.getTopScope().getNameTable().get(identifierString);
        if (nk==null){
            memory.getTopScope().getNameTable().put(identifierString, Memory.NameKind.Accessor);
            return createAndAddGetter(memory, identifierString, body);
        }else switch (nk){
            case Variable:
                memory.getTopScope().getNameTable().put(identifierString, Memory.NameKind.VariableAndAccessor);
                return createAndAddGetter(memory, identifierString, body);
            case Accessor:
            case VariableAndAccessor:
                return createAndAddGetter(memory, identifierString, body);
            case Method:
                throw new NameAlreadyUsedException("Cannot define getter: '"+identifierString+"' is an already used name in this scope.");
            default:
                return null;
        }
    }

    public static ToolMethod createAndAddGetter(Memory memory, String name, RValue body) throws ToolNativeException{

        ToolMethod method = new ToolMethod(
                ToolGetterMethod.METHOD_CATEGORY_GETTER,
                Visibility.Public,
                name,
                new FormalParameter[]{},
                new FormalParameter[]{},
                new ToolBoolean(true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }
}
