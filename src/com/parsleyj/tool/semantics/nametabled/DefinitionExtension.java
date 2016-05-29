package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolField;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.HashMap;

/**
 * Created by Giuseppe on 27/05/16.
 * TODO: javadoc
 */
public class DefinitionExtension implements RValue{

    private RValue klassExpression;
    private RValue body;

    public DefinitionExtension(RValue body, RValue klassExpression) {
        this.body = body;
        this.klassExpression = klassExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject klassObject = klassExpression.evaluate(memory);
        if(!klassObject.getBelongingClass().isOrExtends(memory.baseTypes().C_CLASS))
            throw new InvalidTypeException(memory, "'"+klassExpression+"' is not a valid extensible class.");
        ToolClass oldklass = (ToolClass) klassObject;
        ToolClass newklass = new ToolClass(memory, oldklass.getClassName(),
                oldklass);
        memory.pushClassDefinitionScope(newklass);
        ToolObject bodyResult = body.evaluate(memory);//todo: result could be the default value
        for(Reference r: memory.getTopScope().getReferenceTable().values()){
            newklass.addInstanceField(new ToolField(r.getReferenceType(), r.getIdentifierString(), r.getValue()));
        }
        for(ToolMethod m: memory.getTopScope().getMethods().getAll()){
            newklass.addInstanceMethod(m);
        }
        newklass.setNameTable(new HashMap<>(memory.getTopScope().getNameTable()));
        memory.popScope();
        memory.loadClass(newklass);
        return null;
    }
}
