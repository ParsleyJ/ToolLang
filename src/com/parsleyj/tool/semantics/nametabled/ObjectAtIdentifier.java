package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.CallOnNullException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.exceptions.VisibilityException;
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
    public void assign(ToolObject o, Memory memory) throws ToolNativeException {
        ToolObject owner = leftExp.evaluate(memory);
        if(owner == null || owner.isNull()) throw new CallOnNullException(memory, "Failed trying to call a method with null as owner object.");
        if(!memory.protectedAccessTo(owner)) throw new VisibilityException(memory, "There is not private access from this context to "+owner+" .");

        memory.updateReference(owner.getReferenceMember(ident.getIdentifierString()), o);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject owner = leftExp.evaluate(memory);
        if(owner == null || owner.isNull()) throw new CallOnNullException(memory, "Failed trying to call a method with null as owner object.");
        if(!memory.protectedAccessTo(owner)) throw new VisibilityException(memory, "There is not private access from this context to "+owner+" .");
        return memory.getObjectById(owner.getReferenceMember(ident.getIdentifierString()).getPointedId());
    }

    @Override
    public String toString() {
        return leftExp + "." + ident;
    }
}
