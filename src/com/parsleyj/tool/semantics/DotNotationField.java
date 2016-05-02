package com.parsleyj.tool.semantics;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.ToolObject;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class DotNotationField implements LValue {
    private RValue exp;
    private Identifier ident;

    public DotNotationField(RValue exp, Identifier ident) {
        this.exp = exp;
        this.ident = ident;
    }

    public RValue getUnevaluatedExpression() {
        return exp;
    }

    public Identifier getIdentifier() {
        return ident;
    }

    @Override
    public void assign(ToolObject o, Memory m) throws ToolNativeException {
        ToolObject object = exp.evaluate(m);
        Reference r = object.getReferenceMember(ident.getIdentifierString());
        m.updateReference(r, o);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject object = exp.evaluate(memory);
        Reference r = object.getReferenceMember(ident.getIdentifierString());
        return memory.getObjectById(r.getPointedId());
    }

    @Override
    public String toString() {
        return exp + "." + ident;
    }
}
