package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.NamedLValue;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.util.MethodCall;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class ObjectDotIdentifier implements NamedLValue {
    private RValue leftExp;
    private String ident;

    public ObjectDotIdentifier(RValue leftExp, String ident) {
        this.leftExp = leftExp;
        this.ident = ident;
    }

    public RValue getUnevaluatedExpression() {
        return leftExp;
    }


    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        ToolObject leftExpObject = leftExp.evaluate(m);
        MethodCall setterCall = MethodCall.setter(leftExpObject, ident, o);
        setterCall.evaluate(m);
    }

    @Override
    public ToolObject evaluate(Memory m) throws ToolNativeException {
        ToolObject leftExpObject = leftExp.evaluate(m);
        MethodCall getterCall = MethodCall.getter(leftExpObject, ident);
        return getterCall.evaluate(m);
    }

    @Override
    public String toString() {
        return leftExp + "." + ident;
    }

    @Override
    public String getIdentifierString() {
        return ident;
    }
}
