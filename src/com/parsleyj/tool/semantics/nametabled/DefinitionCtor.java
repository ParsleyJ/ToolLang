package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolCtorMethod;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.flowcontrol.SequentialComposition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 25/05/16.
 * TODO: javadoc
 */
public class DefinitionCtor implements RValue {
    private final RValue body;
    private final List<ParameterDefinition> params;

    public DefinitionCtor(RValue body){
        this.params = new ArrayList<>();
        this.body = body;
    }

    public DefinitionCtor(List<ParameterDefinition> params, RValue body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        DefinitionClass.assertInClassDefinition(memory);
        return createAndAddCtor(memory, params, new ToolBoolean(memory, true), body);
    }

    public static ToolMethod createAndAddCtor(Memory memory, List<ParameterDefinition> params, RValue condition, RValue body) throws ToolNativeException{
        ToolClass klass = memory.getTopScope().getDefinedClass();
        List<FormalParameter> formalParameters = new ArrayList<>();
        for (ParameterDefinition param : params) {
            formalParameters.add(param.defineParameter(memory));
        }
        ToolMethod ctor = new ToolMethod(
                memory,
                ToolCtorMethod.METHOD_CATEGORY_CONSTRUCTOR,
                Visibility.Public,
                ToolCtorMethod.getCtorName(klass),
                new FormalParameter[]{new FormalParameter(Memory.SELF_IDENTIFIER, klass)},
                formalParameters.toArray(new FormalParameter[formalParameters.size()]),
                condition,
                new SequentialComposition(body, Memory::getSelfObject));
        ctor.putDefinitionScope(memory.getCurrentFrameStack());
        klass.addCtor(ctor);
        return ctor;
    }
}
