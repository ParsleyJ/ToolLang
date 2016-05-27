package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;

/**
 * Created by Giuseppe on 24/05/16.
 * TODO: javadoc
 */
@SuppressWarnings("Duplicates")
public class DefinitionOperator implements RValue {

    private ToolOperatorMethod.Mode mode;
    private String operatorSym;
    private String selfIdentifier;
    private RValue argTypeExpression;
    private RValue body;

    private DefinitionOperator(ToolOperatorMethod.Mode mode, String operatorSym, String selfIdentifier, RValue argTypeExpression, RValue body) {
        this.mode = mode;
        this.operatorSym = operatorSym;
        this.selfIdentifier = selfIdentifier;
        this.argTypeExpression = argTypeExpression;
        this.body = body;
    }

    public static DefinitionOperator binary(Identifier selfIdentifier, String operatorSym, RValue argTypeExpression, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Binary, operatorSym, selfIdentifier.getIdentifierString(), argTypeExpression, body);
    }

    public static DefinitionOperator unaryPrefix(String operatorSym, Identifier selfIdentifier, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Prefix, operatorSym, selfIdentifier.getIdentifierString(), null, body);
    }

    public static DefinitionOperator unarySuffix(Identifier selfIdentifier, String operatorSym, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Suffix, operatorSym, selfIdentifier.getIdentifierString(), null, body);
    }

    public static DefinitionOperator binaryParametric(Identifier selfIdentifier, String operatorSym, RValue argTypeExpression, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.BinaryParametric, operatorSym, selfIdentifier.getIdentifierString(), argTypeExpression, body);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        DefinitionClass.assertInClassDefinition(memory);
        if (!selfIdentifier.equals(Memory.SELF_IDENTIFIER))
            throw new InvalidDefinitionException(memory, "The first operand in an operator definition must be always '" + Memory.SELF_IDENTIFIER + "'.");
        switch (mode) {
            case Prefix:
            case Suffix: {
                return createAndAddUnaryOperatorMethod(memory, mode, operatorSym, body);
            }
            case Binary:
            case BinaryParametric: {
                FormalParameter argParam = ExplicitTypeParameterDefinition.getFormalParameter(memory, argTypeExpression, Memory.ARG_IDENTIFIER);
                return createAndAddBinaryOperatorMethod(memory, mode, operatorSym, argParam, body);
            }
            default:
                return null;
        }


    }


    public static ToolMethod createAndAddBinaryOperatorMethod(Memory memory, ToolOperatorMethod.Mode mode, String operatorSym, FormalParameter argParam, RValue body) throws ToolNativeException {
        ToolClass selfType = memory.getTopScope().getDefinedClass();
        ToolMethod method = new ToolMethod(
                memory,
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, selfType)
                },
                new FormalParameter[]{argParam},
                new ToolBoolean(memory, true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }

    public static ToolMethod createAndAddUnaryOperatorMethod(Memory memory, ToolOperatorMethod.Mode mode, String operatorSym, RValue body) throws ToolNativeException {
        ToolClass selfType = memory.getTopScope().getDefinedClass();
        ToolMethod method = new ToolMethod(
                memory,
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, selfType)
                },
                new FormalParameter[]{},
                new ToolBoolean(memory, true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }
}
