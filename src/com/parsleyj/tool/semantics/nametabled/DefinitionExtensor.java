package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolExtensor;
import com.parsleyj.tool.objects.ToolField;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.special.ToolCtorMethod;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.HashMap;

/**
 * Created by Giuseppe on 27/05/16.
 * TODO: javadoc
 */
public class DefinitionExtensor implements RValue{

    private RValue body;

    public DefinitionExtensor(RValue body) {
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolExtensor extensor = new ToolExtensor(memory);
        memory.pushClassDefinitionScope();
        body.evaluate(memory);
        for(Reference r: memory.getTopScope().getReferenceTable().values()){
            extensor.addInstanceField(new ToolField(r.getReferenceType(), r.getIdentifierString(), r.getValue()));
        }
        for(ToolMethod m: memory.getTopScope().getMethods().getAll()){
            if(m.getMethodCategory().equals(ToolCtorMethod.METHOD_CATEGORY_CONSTRUCTOR)) extensor.addCtor(m);
            else extensor.addInstanceMethod(m);

        }
        extensor.setNameTable(new HashMap<>(memory.getTopScope().getNameTable()));
        memory.popScope();
        return extensor;
    }
}
