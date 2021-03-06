package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.NamedLValue;
import com.parsleyj.tool.semantics.util.MethodCall;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class LocalDotIdentifier implements NamedLValue {

    private String identifierString;

    public LocalDotIdentifier(String identifierString) {
        this.identifierString = identifierString;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        MethodCall setterCall = MethodCall.localSetter(identifierString, o);
        setterCall.evaluate(m);
    }

    @Override
    public ToolObject evaluate(Memory m) throws ToolNativeException {
        switch (m.getTopScope().getNameTable().get(identifierString)) {
            case Method:
                throw new UnsupportedOperationException("OverloadedMethodSet for scoped functions still not implemented"); //TODO implement
            default:
                MethodCall getterCall = MethodCall.localGetter(identifierString);
                return getterCall.evaluate(m);

        }
    }

    @Override
    public String toString() {
        return "." + identifierString;
    }

    @Override
    public String getIdentifierString() {
        return identifierString;
    }

}
