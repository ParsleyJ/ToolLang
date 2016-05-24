package com.parsleyj.tool.semantics.nametabled;

import com.parsleyj.tool.exceptions.InvalidDefinitionException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolInterface;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.base.Identifier;
import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.tool.semantics.base.RValue;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 24/05/16.
 * TODO: javadoc
 */
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

    public static DefinitionOperator unaryPrefix(String operatorSym, Identifier selfIdentifier, RValue body){
        return new DefinitionOperator(ToolOperatorMethod.Mode.Prefix, operatorSym, selfIdentifier.getIdentifierString(), null, body);
    }

    public static DefinitionOperator unarySuffix(Identifier selfIdentifier, String operatorSym, RValue body){
        return new DefinitionOperator(ToolOperatorMethod.Mode.Suffix, operatorSym, selfIdentifier.getIdentifierString(), null, body);
    }

    public static DefinitionOperator binaryParametric(Identifier selfIdentifier, String operatorSym, RValue argTypeExpression, RValue body){
        return new DefinitionOperator(ToolOperatorMethod.Mode.BinaryParametric, operatorSym, selfIdentifier.getIdentifierString(), argTypeExpression, body);
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        if(memory.getTopScope().getScopeType() == Memory.Scope.ScopeType.ClassDefinition){
            if(!selfIdentifier.equals(Memory.SELF_IDENTIFIER)) throw new InvalidDefinitionException("The first operand in an operator definition must be always '"+Memory.SELF_IDENTIFIER+"'.");
            switch (mode){
                case Prefix:
                case Suffix:{
                    ToolClass selfType = memory.getTopScope().getDefinedClass();
                    return createAndAddUnaryOperatorMethod(memory, mode, operatorSym, selfType, body);
                }
                case Binary:
                case BinaryParametric:{
                    FormalParameter argParam = ExplicitTypeParameterDefinition.getFormalParameter(memory, argTypeExpression, Memory.ARG_IDENTIFIER);
                    ToolClass selfType = memory.getTopScope().getDefinedClass();
                    return createAndAddBinaryOperatorMethod(memory, mode, operatorSym, selfType, argParam, body);
                }
                default:
                    return null;
            }

        }else{
            throw new InvalidDefinitionException("Operator definitions can only be in a class definition scope.");
        }
    }


    public static ToolMethod createAndAddBinaryOperatorMethod(Memory memory, ToolOperatorMethod.Mode mode, String operatorSym, ToolClass selfType, FormalParameter argParam, RValue body) throws ToolNativeException{
        ToolMethod method = new ToolMethod(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, selfType)
                },
                new FormalParameter[]{argParam},
                new ToolBoolean(true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }

    public static ToolMethod createAndAddUnaryOperatorMethod(Memory memory, ToolOperatorMethod.Mode mode, String operatorSym, ToolClass selfType, RValue body) throws ToolNativeException{
        ToolMethod method = new ToolMethod(
                ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                Visibility.Public,
                ToolOperatorMethod.getOperatorMethodName(mode, operatorSym),
                new FormalParameter[]{
                        new FormalParameter(Memory.SELF_IDENTIFIER, selfType)
                },
                new FormalParameter[]{},
                new ToolBoolean(true),
                body);
        method.putDefinitionScope(memory.getCurrentFrameStack(), memory);
        memory.getTopScope().addMethod(method);
        return method;
    }
}
