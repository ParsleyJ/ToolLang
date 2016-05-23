package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.NameAlreadyUsedException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;

/**
 * Created by Giuseppe on 22/05/16.
 * TODO: javadoc
 */
public class LocalDefinitionVariable implements LValue {

    private String identifierString;

    public LocalDefinitionVariable(String identifierString){
        this.identifierString = identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        Memory.NameKind nk = m.getTopScope().getNameTable().get(identifierString);
        if(nk == null){
            m.getTopScope().getNameTable().put(identifierString, Memory.NameKind.Variable);
            m.newLocalReference(identifierString, o);
        }else switch (nk){
            case Variable:
            case VariableAndAccessor:
                m.newLocalReference(identifierString, o);//which throws an error
                break;
            case Accessor:
                m.getTopScope().getNameTable().put(identifierString, Memory.NameKind.VariableAndAccessor);
                m.newLocalReference(identifierString, o);
                break;
            case Method:
                throw new NameAlreadyUsedException("Cannot create local variable: '"+identifierString+"' is an already used name in this scope.");
        }

    }

    @Override
    public ToolObject evaluate(Memory memory) {
        return BaseTypes.O_NULL;
    } //TODO: a definition can only be assigned (to prevent nullity)

    @Override
    public String toString() {
        return "local " + identifierString;
    }
}
