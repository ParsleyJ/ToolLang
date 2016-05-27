package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.InvalidTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giuseppe on 23/05/16.
 * TODO: javadoc
 */
public class DefinitionClass implements RValue{

    private String name;
    private List<RValue> parentsExpressions;
    private RValue body;

    public DefinitionClass(String name, List<RValue> parents, RValue body) {
        this.name = name;
        this.parentsExpressions = parents;
        this.body = body;
    }

    public static void assertInClassDefinition(Memory memory) throws InvalidDefinitionException {
        if (memory.getTopScope().getScopeType() != Memory.Scope.ScopeType.ClassDefinition)
            throw new InvalidDefinitionException(memory, "Operator or Ctor definitions can only be in a class/extension definition scope.");
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {

        ToolClass parentClass;
        List<ToolInterface> interfaces = new ArrayList<>();
        if(parentsExpressions.isEmpty()){
            parentClass = memory.baseTypes().C_OBJECT;
        } else {
            ToolObject o = parentsExpressions.get(0).evaluate(memory);
            if(o.getBelongingClass().isOrExtends(memory.baseTypes().C_CLASS)){
                parentClass = (ToolClass) o;
            }else if(o.getBelongingClass().isOrExtends(memory.baseTypes().C_INTERFACE)){
                parentClass = memory.baseTypes().C_OBJECT;
                interfaces.add((ToolInterface) o);
            }else{
                throw new InvalidTypeException(memory,"'"+parentsExpressions.get(0)+"' is not a valid class or interface");
            }

            for(int i = 1; i < parentsExpressions.size(); ++i){
                interfaces.add(evalAsInterface(parentsExpressions.get(i), memory));
            }
        }


        ToolClass klass = new ToolClass(memory,
                name, parentClass,
                interfaces.toArray(new ToolInterface[interfaces.size()]));
        memory.pushClassDefinitionScope(klass);
        ToolObject bodyResult = body.evaluate(memory);//todo: result could be the default value
        for(Reference r: memory.getTopScope().getReferenceTable().values()){
            klass.addInstanceField(new ToolField(r.getReferenceType(), r.getIdentifierString(), memory.getObjectById(r.getPointedId())));
        }
        for(ToolMethod m: memory.getTopScope().getMethods().getAll()){
            klass.addInstanceMethod(m);
        }
        klass.setNameTable(new HashMap<>(memory.getTopScope().getNameTable()));
        memory.popScopeAndGC();
        //FIXME: check if class actually implements methods declared in explicitly implemented interfaces
        memory.loadClass(klass);
        return klass;
    }



    public static ToolInterface evalAsInterface(RValue interfaceExpression, Memory memory) throws ToolNativeException{
        ToolObject type = interfaceExpression.evaluate(memory);
        if(type.getBelongingClass().isOrExtends(memory.baseTypes().C_INTERFACE)) {
            return (ToolInterface) type;
        }else{
            throw new InvalidTypeException(memory, "'"+interfaceExpression+"' is not a valid interface");
        }
    }

}
