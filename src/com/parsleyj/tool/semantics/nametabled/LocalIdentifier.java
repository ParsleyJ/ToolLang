package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.NameNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.utils.Pair;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class LocalIdentifier implements Identifier {
    private String identifierString;

    public LocalIdentifier(String identifierString) {
        this.identifierString = identifierString;
    }


    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        Pair<Memory.NameKind, Memory.Scope> queryResult = memory.recursivelyGetNameKind(identifierString);
        if(queryResult == null || queryResult.getFirst() == null){
            throw new NameNotFoundException(memory, "Name '"+identifierString+"' not found.");
        }else switch(queryResult.getFirst()){
            case Variable:
            case Method:
                return memory.getObjectByIdentifier(identifierString); //probably throws an error when there is no variable
            case Accessor:
            case VariableAndAccessor:
                return MethodCall.localGetter(identifierString).evaluate(memory);
            default:
                return null;
        }
    }

    @Override
    public String getIdentifierString() {
        return identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        Memory.NameKind nk = m.getTopScope().getNameTable().get(identifierString);
        if(nk == null){
            m.updateReference(identifierString, o); //throws an error
        }else switch(nk){
            case Variable:
            case Method:
                m.updateReference(identifierString, o); //probably throws an error when there is no variable
            case Accessor:
            case VariableAndAccessor:
                MethodCall.localSetter(identifierString, o).evaluate(m);
            default:
        }

    }

    @Override
    public String toString() {
        return identifierString;
    }

    @Override
    public FormalParameter defineParameter(Memory memory) throws ToolNativeException {
        return new FormalParameter(getIdentifierString(), memory.baseTypes().C_OBJECT);

    }
}
