package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class DotNotationField implements LValue {
    private RValue leftExp;
    private Identifier ident;

    public DotNotationField(RValue leftExp, Identifier ident) {
        this.leftExp = leftExp;
        this.ident = ident;
    }

    public RValue getUnevaluatedExpression() {
        return leftExp;
    }

    public Identifier getIdentifier() {
        return ident;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        ToolObject leftExpObject = leftExp.evaluate(m);
        MethodCall setterCall = MethodCall.setter(leftExpObject, ident.getIdentifierString(), o);
        setterCall.evaluate(m);
    }

    @Override
    public ToolObject evaluate(Memory m) throws ToolNativeException {
        ToolObject leftExpObject = leftExp.evaluate(m);
        MethodCall getterCall = MethodCall.getter(leftExpObject, ident.getIdentifierString());
        return getterCall.evaluate(m);
    }

    @Override
    public String toString() {
        return leftExp + "." + ident;
    }
}
