package com.parsleyj.tool.semantics.nametabled;

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
public class ClassDefinition implements RValue{

    private String name;
    private RValue parentTypeExpression;
    private List<RValue> explicitImplementsExpressions;
    private RValue body;

    public ClassDefinition(String name, List<RValue> parents, RValue body) {
        this.name = name;
        if(parents.isEmpty()){
            parentTypeExpression = BaseTypes.C_OBJECT;
            explicitImplementsExpressions = new ArrayList<>();
        } else {
            this.parentTypeExpression = parents.get(0);
            explicitImplementsExpressions = new ArrayList<>();
            for(int i = 1; i < parents.size(); ++i){
                explicitImplementsExpressions.add(parents.get(i));
            }
        }
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        List<ToolInterface> interfaces = new ArrayList<>();
        for (RValue interfaceExpr: explicitImplementsExpressions) {
            interfaces.add(evalAsInterface(interfaceExpr, memory));
        }
        ToolClass klass = new ToolClass(
                name, evalAsClass(parentTypeExpression, memory),
                interfaces.toArray(new ToolInterface[interfaces.size()]));
        memory.pushScope();
        ToolObject bodyResult = body.evaluate(memory);//todo: result can be the default value
        for(Reference r: memory.getTopScope().getReferenceTable().values()){
            klass.addInstanceField(new ToolField(r.getReferenceType(), r.getIdentifierString(), memory.getObjectById(r.getPointedId())));
        }
        for(ToolMethod m: memory.getTopScope().getMethods().getAll()){
            klass.addInstanceMethod(m);
        }
        klass.setNameTable(new HashMap<>(memory.getTopScope().getNameTable()));
        memory.popScopeAndGC();
        memory.loadClasses(Collections.singletonList(klass));
        return klass;
    }

    public static ToolClass evalAsClass(RValue typeExpression, Memory memory) throws ToolNativeException{
        ToolObject type = typeExpression.evaluate(memory);
        if(type.getBelongingClass().isOrExtends(BaseTypes.C_CLASS)) {
            return (ToolClass) type;
        }else{
            throw new InvalidTypeException("'"+typeExpression+"' is not a valid class");
        }
    }

    public static ToolInterface evalAsInterface(RValue interfaceExpression, Memory memory) throws ToolNativeException{
        ToolObject type = interfaceExpression.evaluate(memory);
        if(type.getBelongingClass().isOrExtends(BaseTypes.C_INTERFACE)) {
            return (ToolInterface) type;
        }else{
            throw new InvalidTypeException("'"+interfaceExpression+"' is not a valid interface");
        }
    }

}
