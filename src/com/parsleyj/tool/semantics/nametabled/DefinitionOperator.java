package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;

import java.util.Objects;

/**
 * Created by Giuseppe on 24/05/16.
 * TODO: javadoc
 */
@SuppressWarnings("Duplicates")
public class DefinitionOperator implements RValue {

    private ToolOperatorMethod.Mode mode;
    private String operatorSym;
    private RValue selfExpression;
    private RValue argTypeExpression;
    private RValue body;

    private DefinitionOperator(ToolOperatorMethod.Mode mode, String operatorSym, RValue selfExpression, RValue argTypeExpression, RValue body) {
        this.mode = mode;
        this.operatorSym = operatorSym;
        this.selfExpression = selfExpression;
        this.argTypeExpression = argTypeExpression;
        this.body = body;
    }

    public static DefinitionOperator binary(RValue selfExpression, String operatorSym, RValue argTypeExpression, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Binary, operatorSym, selfExpression, argTypeExpression, body);
    }

    public static DefinitionOperator unaryPrefix(String operatorSym, RValue selfExpression, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Prefix, operatorSym, selfExpression, null, body);
    }

    public static DefinitionOperator unarySuffix(RValue selfExpression, String operatorSym, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.Suffix, operatorSym, selfExpression, null, body);
    }

    public static DefinitionOperator binaryParametric(RValue selfExpression, String operatorSym, RValue argTypeExpression, RValue body) {
        return new DefinitionOperator(ToolOperatorMethod.Mode.BinaryParametric, operatorSym, selfExpression, argTypeExpression, body);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        DefinitionClass.assertInClassDefinition(memory);
        if (!Objects.equals(selfExpression.evaluate(memory).getId(), memory.getSelfObject().getId()))
            throw new InvalidDefinitionException(memory, "The one operand in an operator definition must be always 'this'.");
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
        ToolMethod method = new ToolMethod(
                memory,
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{},
                new FormalParameter[]{argParam},
                new ToolBoolean(memory, true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack());
        memory.getTopScope().addMethod(method);
        return method;
    }

    public static ToolMethod createAndAddUnaryOperatorMethod(Memory memory, ToolOperatorMethod.Mode mode, String operatorSym, RValue body) throws ToolNativeException {
        ToolMethod method = new ToolMethod(
                memory,
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{},
                new FormalParameter[]{},
                new ToolBoolean(memory, true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack());
        memory.getTopScope().addMethod(method);
        return method;
    }
}
