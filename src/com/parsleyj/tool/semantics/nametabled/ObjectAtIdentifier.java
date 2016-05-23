package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.LValue;
import com.parsleyj.tool.semantics.base.RValue;

/**
 * Created by Giuseppe on 22/05/16.
 * TODO: javadoc
 */
public class ObjectAtIdentifier implements LValue{
    private RValue leftExp;
    private Identifier ident;

    public ObjectAtIdentifier(RValue leftExp, Identifier ident) {
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
    public void assign(ToolObject o, Memory m) throws ToolNativeException { //TODO: check private access
        ToolObject leftExpObject = leftExp.evaluate(m);
        m.updateReference(leftExpObject.getReferenceMember(ident.getIdentifierString()), o);
    }

    @Override
    public ToolObject evaluate(Memory m) throws ToolNativeException {
        ToolObject leftExpObject = leftExp.evaluate(m);
        return m.getObjectById(leftExpObject.getReferenceMember(ident.getIdentifierString()).getPointedId());
    }

    @Override
    public String toString() {
        return leftExp + "." + ident;
    }
}
