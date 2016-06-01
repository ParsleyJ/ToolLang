package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.NameAlreadyUsedException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.NamedLValue;

/**
 * Created by Giuseppe on 22/05/16.
 * TODO: javadoc
 */
public class DefinitionVariable implements NamedLValue {

    private String identifierString;

    public DefinitionVariable(String identifierString){
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
                throw new NameAlreadyUsedException(m, "Cannot create local variable: '"+identifierString+"' is an already used name in this scope.");
        }

    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        throw new InvalidDefinitionException(memory, "Variable declaration cannot be used as r-value");
    }

    @Override
    public String toString() {
        return "local " + identifierString;
    }

    @Override
    public String getIdentifierString() {
        return identifierString;
    }
}
