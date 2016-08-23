package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.semantics.base.*;
import com.parsleyj.tool.semantics.expr.*;
import com.parsleyj.tool.semantics.flowcontrol.*;
import com.parsleyj.tool.semantics.nametabled.*;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;
import com.parsleyj.tool.semantics.parameter.ParameterDefinitionListImpl;
import com.parsleyj.tool.semantics.tuples.LValueListBetweenBrackets;
import com.parsleyj.tool.semantics.tuples.RValueListBetweenBrackets;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.toolparser.parser.Associativity;
import com.parsleyj.toolparser.parser.SyntaxClass;
import com.parsleyj.toolparser.program.*;
import com.parsleyj.toolparser.semanticsconverter.CBOConverterMethod;
import com.parsleyj.toolparser.semanticsconverter.TokenConverter;
import com.parsleyj.toolparser.semanticsconverter.UBOConverterMethod;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.toolparser.semanticsconverter.SimpleWrapConverterMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public class ToolGrammar {

    public static SyntaxClass param = new SyntaxClass("param");
    public static SyntaxClass paramList = new SyntaxClass("paramList");

    public static SyntaxClass rExp = new SyntaxClass("rExp");
    public static SyntaxClass rExpList = new SyntaxClass("rExpList");

    public static SyntaxClass lExp = new SyntaxClass("lExp", rExp); //lExp "extends" rExp = lExp can be treated as rExp
    public static SyntaxClass lExpList = new SyntaxClass("lExpList", rExpList);

    public static SyntaxClass ident = new SyntaxClass("ident", lExp, param);
    public static SyntaxClass identList = new SyntaxClass("identList", lExpList, paramList);

    public static Interpreter getDefaultInterpreter(Memory memory) {
        TokenCategoryDefinition stringToken = new TokenCategoryDefinition("STRING", "([\"'])(?:(?=(\\\\?))\\2.)*?\\1",
                (g) -> ToolString.newFromLiteral(memory, g));
        TokenCategoryDefinition nullToken = new TokenCategoryDefinition("NULL_KEYWORD", "\\Qnull\\E",
                (g) -> memory.baseTypes().O_NULL);
        TokenCategoryDefinition trueToken = new TokenCategoryDefinition("TRUE_KEYWORD", "\\Qtrue\\E",
                (g) -> new ToolBoolean(memory,true));
        TokenCategoryDefinition falseToken = new TokenCategoryDefinition("FALSE_KEYWORD", "\\Qfalse\\E",
                (g) -> new ToolBoolean(memory,false));
        TokenCategoryDefinition whileToken = new TokenCategoryDefinition("WHILE_KEYWORD", "\\Qwhile\\E");
        TokenCategoryDefinition forToken = new TokenCategoryDefinition("FOR_KEYWORD", "\\Qfor\\E");
        TokenCategoryDefinition inToken = new TokenCategoryDefinition("IN_KEYWORD", "\\Qin\\E");
        TokenCategoryDefinition doToken = new TokenCategoryDefinition("DO_KEYWORD", "\\Qdo\\E");
        TokenCategoryDefinition ifToken = new TokenCategoryDefinition("IF_KEYWORD", "\\Qif\\E");
        TokenCategoryDefinition thenToken = new TokenCategoryDefinition("THEN_KEYWORD", "\\Qthen\\E");
        TokenCategoryDefinition elseToken = new TokenCategoryDefinition("ELSE_KEYWORD", "\\Qelse\\E");
        TokenCategoryDefinition breakToken = new TokenCategoryDefinition("BREAK_KEYWORD", "\\Qbreak\\E");
        TokenCategoryDefinition printOperatorToken = new TokenCategoryDefinition("PRINT_OPERATOR", "\\Qprint\\E");
        TokenCategoryDefinition andOperatorToken = new TokenCategoryDefinition("AND_OPERATOR", "\\Qand\\E");
        TokenCategoryDefinition orOperatorToken = new TokenCategoryDefinition("OR_OPERATOR", "\\Qor\\E");
        TokenCategoryDefinition notOperatorToken = new TokenCategoryDefinition("NOT_OPERATOR", "\\Qnot\\E");
        TokenCategoryDefinition defToken = new TokenCategoryDefinition("DEF_KEYWORD", "\\Qdef\\E");
        TokenCategoryDefinition localToken = new TokenCategoryDefinition("LOCAL_KEYWORD", "\\Qlocal\\E");
        TokenCategoryDefinition getterToken = new TokenCategoryDefinition("GETTER_KEYWORD", "\\Qgetter\\E");
        TokenCategoryDefinition setterToken = new TokenCategoryDefinition("SETTER_KEYWORD", "\\Qsetter\\E");
        TokenCategoryDefinition operatorToken = new TokenCategoryDefinition("OPERATOR_KEYWORD", "\\Qoperator\\E");
        TokenCategoryDefinition ctorToken = new TokenCategoryDefinition("CTOR_KEYWORD", "\\Qctor\\E");
        TokenCategoryDefinition extensionToken = new TokenCategoryDefinition("EXTENSION_KEYWORD", "\\Qextension\\E");
        TokenCategoryDefinition extensorToken = new TokenCategoryDefinition("EXTENSOR_KEYWORD", "\\Qextensor\\E");
        TokenCategoryDefinition valToken = new TokenCategoryDefinition("VAL_KEYWORD", "\\Qval\\E");
        TokenCategoryDefinition varToken = new TokenCategoryDefinition("VAR_KEYWORD", "\\Qvar\\E");
        TokenCategoryDefinition thisToken = new TokenCategoryDefinition("THIS_KEYWORD", "\\Qthis\\E",
                (g) -> (RValue) Memory::getSelfObject);
        TokenCategoryDefinition isToken = new TokenCategoryDefinition("IS_OPERATOR", "\\Qis\\E");
        TokenCategoryDefinition classToken = new TokenCategoryDefinition("CLASS_KEYWORD", "\\Qclass\\E");
        TokenCategoryDefinition identifierToken = new TokenCategoryDefinition("IDENTIFIER", "[_a-zA-Z][_a-zA-Z0-9]*",
                LocalIdentifier::new);
        MultiPatternDefinition identifierMultiPattern = new MultiPatternDefinition("[_a-zA-Z][_a-zA-Z0-9]*") {
            @Override
            public List<TokenConverter> getDeclaredTokenConverters() {
                return getConverters(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken,
                        breakToken, printOperatorToken,
                        andOperatorToken, orOperatorToken, notOperatorToken,
                        defToken, getterToken, setterToken, operatorToken, ctorToken,
                        localToken, valToken, varToken,
                        classToken, extensionToken, extensorToken,
                        thisToken,
                        isToken,
                        identifierToken);
            }

            @Override
            public TokenCategory generateTokenCategory(String matchedString) {
                switch (matchedString){
                    case "null": return nullToken;
                    case "true": return trueToken;
                    case "false": return falseToken;
                    case "while": return whileToken;
                    case "for": return  forToken;
                    case "in": return inToken;
                    case "do": return doToken;
                    case "if": return ifToken;
                    case "then": return thenToken;
                    case "else": return elseToken;
                    case "break": return breakToken;
                    case "print": return printOperatorToken;
                    case "and": return andOperatorToken;
                    case "or": return orOperatorToken;
                    case "not": return notOperatorToken;
                    case "def": return defToken;
                    case "getter": return getterToken;
                    case "setter": return setterToken;
                    case "operator": return operatorToken;
                    case "ctor": return ctorToken;
                    case "local": return localToken;
                    case "val": return valToken;
                    case "var": return varToken;
                    case "class": return classToken;
                    case "extension": return extensionToken;
                    case "extensor": return extensorToken;
                    case "this": return thisToken;
                    case "is": return isToken;
                    default: return identifierToken;
                }
            }

            @Override
            public List<TokenCategory> declaredTokenCategories() {
                return Arrays.asList(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken,
                        breakToken, printOperatorToken,
                        andOperatorToken, orOperatorToken, notOperatorToken,
                        defToken, getterToken, setterToken, operatorToken, ctorToken,
                        localToken, valToken, varToken,
                        classToken, extensionToken, extensorToken,
                        thisToken,
                        isToken,
                        identifierToken);
            }
        };
        TokenCategoryDefinition doubleDotToken = new TokenCategoryDefinition("DOUBLE_DOT", "\\Q..\\E");
        TokenCategoryDefinition destructuralAssignmentOperatorToken = new TokenCategoryDefinition(
                "DESTRUCTURAL_ASSIGNMENT_OPERATOR", "\\Q:=\\E");
        TokenCategoryDefinition tagSetOperatorToken = new TokenCategoryDefinition("TAG_SET_OPERATOR", "\\Q@@:\\E");
        TokenCategoryDefinition tagDereferenceOperatorToken = new TokenCategoryDefinition("TAG_DEREFERENCE_OPERATOR", "\\Q@@\\E");
        TokenCategoryDefinition sameInstanceOperatorToken = new TokenCategoryDefinition("SAME_INSTANCE_OPERATOR", "\\Q===\\E");
        TokenCategoryDefinition equalsOperatorToken = new TokenCategoryDefinition("EQUALS_OPERATOR", "\\Q==\\E");
        TokenCategoryDefinition notEqualsOperatorToken = new TokenCategoryDefinition("NOT_EQUALS_OPERATOR", "\\Q!=\\E");
        TokenCategoryDefinition equalGreaterOperatorToken = new TokenCategoryDefinition("EQUAL_GREATER_OPERATOR", "\\Q>=\\E");
        TokenCategoryDefinition equalLessOperatorToken = new TokenCategoryDefinition("EQUAL_LESS_OPERATOR", "\\Q<=\\E");
        TokenCategoryDefinition atToken = new TokenCategoryDefinition("AT", "\\Q@\\E");
        TokenCategoryDefinition dotToken = new TokenCategoryDefinition("DOT", "\\Q.\\E");
        TokenCategoryDefinition colonToken = new TokenCategoryDefinition("COLON", "\\Q:\\E");
        TokenCategoryDefinition commaToken = new TokenCategoryDefinition("COMMA", "\\Q,\\E");
        TokenCategoryDefinition slashToken = new TokenCategoryDefinition("SLASH", "\\Q/\\E");
        TokenCategoryDefinition exclamationPointToken = new TokenCategoryDefinition("EXCLAMATION_POINT", "\\Q!\\E");
        TokenCategoryDefinition plusToken = new TokenCategoryDefinition("PLUS", "\\Q+\\E");
        TokenCategoryDefinition minusToken = new TokenCategoryDefinition("MINUS", "\\Q-\\E");
        TokenCategoryDefinition asteriskToken = new TokenCategoryDefinition("ASTERISK", "\\Q*\\E");
        TokenCategoryDefinition percentSignToken = new TokenCategoryDefinition("PERCENT_SIGN", "\\Q%\\E");
        TokenCategoryDefinition assignmentOperatorToken = new TokenCategoryDefinition("ASSIGNMENT_OPERATOR", "\\Q=\\E");
        TokenCategoryDefinition greaterOperatorToken = new TokenCategoryDefinition("GREATER_OPERATOR", "\\Q>\\E");
        TokenCategoryDefinition lessOperatorToken = new TokenCategoryDefinition("LESS_OPERATOR", "\\Q<\\E");
        TokenCategoryDefinition semicolonToken = new TokenCategoryDefinition("SEMICOLON", "\\Q;\\E");
        TokenCategoryDefinition openRoundBracketToken = new TokenCategoryDefinition("OPEN_ROUND_BRACKET", "\\Q(\\E");
        TokenCategoryDefinition closedRoundBracketToken = new TokenCategoryDefinition("CLOSED_ROUND_BRACKET", "\\Q)\\E");
        TokenCategoryDefinition openCurlyBracketToken = new TokenCategoryDefinition("OPEN_CURLY_BRACKET", "\\Q{\\E");
        TokenCategoryDefinition closedCurlyBracketToken = new TokenCategoryDefinition("CLOSED_CURLY_BRACKET", "\\Q}\\E");
        TokenCategoryDefinition openSquareBracketToken = new TokenCategoryDefinition("OPEN_SQUARE_BRACKET", "\\Q[\\E");
        TokenCategoryDefinition closedSquareBracketToken = new TokenCategoryDefinition("CLOSED_SQUARE_BRACKET", "\\Q]\\E");
        TokenCategoryDefinition numeralToken = new TokenCategoryDefinition("NUMERAL", "(0|([1-9]\\d*))",
                // "(?<=\\s|^)[-+]?\\d+(?=\\s|$)"
                g -> new ToolInteger(memory, Integer.decode(g)));
        TokenCategoryDefinition newLineToken = new TokenCategoryDefinition("NEWLINE", "\\Q\n\\E", true);
        TokenCategoryDefinition blankToken = new TokenCategoryDefinition("BLANK", "\\s+", true);

        LexicalPatternDefinition[] lexicon = new LexicalPatternDefinition[]{
                stringToken,
                identifierMultiPattern,
                tagSetOperatorToken,
                tagDereferenceOperatorToken,
                atToken, doubleDotToken, dotToken,
                destructuralAssignmentOperatorToken,
                colonToken, commaToken,
                exclamationPointToken,
                plusToken, minusToken, asteriskToken, slashToken, percentSignToken,
                sameInstanceOperatorToken,
                equalsOperatorToken, notEqualsOperatorToken,
                greaterOperatorToken, equalGreaterOperatorToken,
                lessOperatorToken, equalLessOperatorToken,
                assignmentOperatorToken,
                semicolonToken,
                openRoundBracketToken, closedRoundBracketToken,
                openCurlyBracketToken, closedCurlyBracketToken,
                openSquareBracketToken, closedSquareBracketToken,
                numeralToken,
                newLineToken,
                blankToken,
        };


        SyntaxCaseDefinition nullLiteral = new SyntaxCaseDefinition(rExp, "nullLiteral",
                new SimpleWrapConverterMethod(),
                nullToken);
        SyntaxCaseDefinition trueConst = new SyntaxCaseDefinition(rExp, "trueConst",
                new SimpleWrapConverterMethod(),
                trueToken);
        SyntaxCaseDefinition falseConst = new SyntaxCaseDefinition(rExp, "falseConst",
                new SimpleWrapConverterMethod(),
                falseToken);
        SyntaxCaseDefinition numeral = new SyntaxCaseDefinition(rExp, "numeral",
                new SimpleWrapConverterMethod(),
                numeralToken);
        SyntaxCaseDefinition string = new SyntaxCaseDefinition(rExp, "string",
                new SimpleWrapConverterMethod(),
                stringToken);
        SyntaxCaseDefinition thisReference = new SyntaxCaseDefinition(rExp, "thisReference",
                new SimpleWrapConverterMethod(),
                thisToken);
        SyntaxCaseDefinition identifier = new SyntaxCaseDefinition(ident, "identifier",
                new SimpleWrapConverterMethod(),
                identifierToken);

        SyntaxCaseDefinition expressionBetweenRoundBrackets = new SyntaxCaseDefinition(rExp, "expressionBetweenRoundBrackets",
                (n, s) -> new ExpressionBlock(s.convert(n.get(1))),
                openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition expressionBetweenCurlyBrackets = new SyntaxCaseDefinition(rExp, "expressionBetweenCurlyBrackets",
                (n, s) -> new ScopedBlock(s.convert(n.get(1))),
                openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition parameterDeclaration = new SyntaxCaseDefinition(param, "parameterDeclaration",
                (n, s) -> new ExplicitTypeParameterDefinition(s.convert(n.get(0)), s.convert(n.get(2))),
                ident, colonToken, rExp);

        SyntaxCaseDefinition rValueListBetweenBrackets = new SyntaxCaseDefinition(rExp, "rValueListBetweenBrackets",
                (n, s) -> new RValueListBetweenBrackets((RValueList) s.convert(n.get(1))),
                openRoundBracketToken, rExpList, closedRoundBracketToken);
        SyntaxCaseDefinition lValueListBetweenBrackets1 = new SyntaxCaseDefinition(lExp, "lValueListBetweenBrackets1",
                (n, s) -> new LValueListBetweenBrackets((LValue) s.convert(n.get(1))),
                openRoundBracketToken, lExp, closedRoundBracketToken);
        SyntaxCaseDefinition lValueListBetweenBrackets2 = new SyntaxCaseDefinition(lExp, "lValueListBetweenBrackets2",
                (n, s) -> new LValueListBetweenBrackets((LValueList) s.convert(n.get(1))),
                openRoundBracketToken, lExpList, closedRoundBracketToken);


        SyntaxCaseDefinition localCall0 = new SyntaxCaseDefinition(rExp, "localCall0",
                (n, s) -> new LocalCall(
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{}),
                ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition localCall1 = new SyntaxCaseDefinition(rExp, "localCall1",
                (n, s) -> new LocalCall(
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{s.convert(n.get(2))}),
                ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition localCall2 = new SyntaxCaseDefinition(rExp, "localCall2",
                (n, s) -> new LocalCall(
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        ((RValueList) s.convert(n.get(2))).getUnevaluatedArray()),
                ident, openRoundBracketToken, rExpList, closedRoundBracketToken);

        SyntaxCaseDefinition localDotIdentifier = new SyntaxCaseDefinition(lExp, "localDotIdentifier",
                (n, s) -> new LocalDotIdentifier(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                dotToken, ident).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition localDotCall0 = new SyntaxCaseDefinition(rExp, "localDotCall0",
                (n, s) -> new LocalDotCall(((Identifier) s.convert(n.get(1))).getIdentifierString(), new RValue[]{}),
                dotToken, ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition localDotCall1 = new SyntaxCaseDefinition(rExp, "localDotCall1",
                (n, s) -> new LocalDotCall(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        new RValue[]{s.convert(n.get(3))}),
                dotToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition localDotCall2 = new SyntaxCaseDefinition(rExp, "localDotCall2",
                (n, s) -> new LocalDotCall(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        ((RValueList) s.convert(n.get(3))).getUnevaluatedArray()),
                dotToken, ident, openRoundBracketToken, rExpList, closedRoundBracketToken);

        SyntaxCaseDefinition localAtIdentifier = new SyntaxCaseDefinition(lExp, "localAtIdentifier",
                (n, s) -> new LocalAtIdentifier(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                atToken, ident).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition localAtCall0 = new SyntaxCaseDefinition(rExp, "localAtCall0",
                (n, s) -> new LocalAtCall(((Identifier) s.convert(n.get(1))).getIdentifierString(), new RValue[]{}),
                atToken, ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition localAtCall1 = new SyntaxCaseDefinition(rExp, "localAtCall1",
                (n, s) -> new LocalAtCall(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        new RValue[]{s.convert(n.get(3))}),
                atToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition localAtCall2 = new SyntaxCaseDefinition(rExp, "localAtCall2",
                (n, s) -> new LocalAtCall(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        ((RValueList) s.convert(n.get(3))).getUnevaluatedArray()),
                atToken, ident, openRoundBracketToken, rExpList, closedRoundBracketToken);


        SyntaxCaseDefinition objectDotIdentifier = new SyntaxCaseDefinition(lExp, "objectDotIdentifier",
                (n, s) -> new ObjectDotIdentifier(s.convert(n.get(0)), ((Identifier) s.convert(n.get(2))).getIdentifierString()),
                rExp, dotToken, ident);
        SyntaxCaseDefinition objectDotCall0 = new SyntaxCaseDefinition(rExp, "objectDotCall0",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectDotCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{});
                },
                rExp, dotToken, ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition objectDotCall1 = new SyntaxCaseDefinition(rExp, "objectDotCall1",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectDotCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{s.convert(n.get(4))});
                },
                rExp, dotToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition objectDotCall2 = new SyntaxCaseDefinition(rExp, "objectDotCall2",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectDotCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            ((RValueList) s.convert(n.get(4))).getUnevaluatedArray());
                },
                rExp, dotToken, ident, openRoundBracketToken, rExpList, closedRoundBracketToken);
        SyntaxCaseDefinition localDefinitionVariable = new SyntaxCaseDefinition(lExp, "localDefinitionVariable",
                (n, s) -> new DefinitionVariable(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                localToken, ident).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition valDefinitionProperty = new SyntaxCaseDefinition(lExp, "valDefinitionProperty",
                (n, s) -> new DefinitionPropertyVal(s.convert(n.get(1))),
                valToken, ident);
        SyntaxCaseDefinition varDefinitionProperty = new SyntaxCaseDefinition(lExp, "varDefinitionProperty",
                (n, s) -> new DefinitionPropertyVar(s.convert(n.get(1))),
                varToken, ident);

        SyntaxCaseDefinition objectAtIdentifier = new SyntaxCaseDefinition(lExp, "objectAtIdentifier",
                (n, s) -> new ObjectAtIdentifier(s.convert(n.get(0)), s.convert(n.get(2))),
                rExp, atToken, ident);
        SyntaxCaseDefinition objectAtCall0 = new SyntaxCaseDefinition(rExp, "objectAtCall0",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectAtCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{});
                },
                rExp, atToken, ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition objectAtCall1 = new SyntaxCaseDefinition(rExp, "objectAtCall1",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectAtCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{s.convert(n.get(4))});
                },
                rExp, atToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition objectAtCall2 = new SyntaxCaseDefinition(rExp, "objectAtCall2",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new ObjectAtCall(
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            ((RValueList) s.convert(n.get(4))).getUnevaluatedArray());
                },
                rExp, atToken, ident, openRoundBracketToken, rExpList, closedRoundBracketToken);


        SyntaxCaseDefinition arrayLiteral0 = new SyntaxCaseDefinition(rExp, "arrayLiteral0",
                (n, s) -> (RValue) m -> new ToolList(m, new ArrayList<>()),
                openSquareBracketToken, closedSquareBracketToken);
        SyntaxCaseDefinition arrayLiteral1 = new SyntaxCaseDefinition(rExp, "arrayLiteral1",
                (n, s) -> (RValue) m -> new ToolList(m, new ArrayList<>(Collections.singletonList(((RValue)s.convert(n.get(1))).evaluate(m)))),
                openSquareBracketToken, rExp, closedSquareBracketToken);
        SyntaxCaseDefinition arrayLiteral2 = new SyntaxCaseDefinition(rExp, "arrayLiteral2",
                (n, s) -> (RValue) m -> {
                    RValueList cselist = s.convert(n.get(1));
                    return cselist.generateToolList(m);
                },
                openSquareBracketToken, rExpList, closedSquareBracketToken);

        SyntaxCaseDefinition typeAccessOperation1 = new SyntaxCaseDefinition(rExp, "typeAccessOperation1",
                (n, s) -> MethodCall.binaryParametricOperator(s.convert(n.get
                        (0)), "<", s.convert(n.get(2)), ">"),
                rExp, lessOperatorToken, rExp, greaterOperatorToken);
        SyntaxCaseDefinition typeAccessOperation2 = new SyntaxCaseDefinition(rExp, "typeAccessOperation2",
                (n, s) -> (RValue) mem -> MethodCall.binaryParametricOperator(
                        s.convert(n.get(0)), "<", ((RValueList) s.convert(n.get(2))).generateToolTuple(mem), ">").evaluate(memory),
                rExp, lessOperatorToken, rExpList, greaterOperatorToken);
        SyntaxCaseDefinition elementAccessOperation1 = new SyntaxCaseDefinition(lExp, "elementAccessOperation1",
                (n, s) -> new LValue() {
                    @Override
                    public void assign(ToolObject o, Memory m) throws ToolNativeException {
                        MethodCall.ternaryOperator(s.convert(n.get(0)), "[", s.convert(n.get(2)), "]=", o).evaluate(m);
                    }
                    @Override
                    public ToolObject evaluate(Memory memory) throws ToolNativeException {
                        return MethodCall.binaryParametricOperator(s.convert(n.get(0)), "[", s.convert(n.get(2)), "]")
                                .evaluate(memory);
                    }
                },
                rExp, openSquareBracketToken, rExp, closedSquareBracketToken);
        SyntaxCaseDefinition elementAccessOperation2 = new SyntaxCaseDefinition(lExp, "elementAccessOperation2",
                (n, s) -> new LValue() {
                    @Override
                    public void assign(ToolObject o, Memory m) throws ToolNativeException {
                        MethodCall.ternaryOperator(s.convert(n.get(0)), "[",
                                ((RValueList) s.convert(n.get(2))).generateToolTuple(memory), "]=", o).evaluate(m);
                    }
                    @Override
                    public ToolObject evaluate(Memory memory) throws ToolNativeException {
                        return MethodCall.binaryParametricOperator(
                                s.convert(n.get(0)), "[", ((RValueList) s.convert(n.get(2))).generateToolTuple(memory), "]")
                                .evaluate(memory);
                    }
                },
                rExp, openSquareBracketToken, rExpList, closedSquareBracketToken);

        SyntaxCaseDefinition unaryMinusOperation = new SyntaxCaseDefinition(rExp, "unaryMinus",
                (n, s) -> MethodCall.prefixOperator("-", s.convert(n.get(1))),
                minusToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition logicalNotOperation = new SyntaxCaseDefinition(rExp, "logicalNotOperation",
                (n, s) -> MethodCall.prefixOperator("!", s.convert(n.get(1))),
                exclamationPointToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition asteriskOperation = new SyntaxCaseDefinition(rExp, "asteriskOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "*", b)),
                rExp, asteriskToken, rExp);
        SyntaxCaseDefinition slashOperation = new SyntaxCaseDefinition(rExp, "slashOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "/", b)),
                rExp, slashToken, rExp);
        SyntaxCaseDefinition percentSignOperation = new SyntaxCaseDefinition(rExp, "percentSignOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "%", b)),
                rExp, percentSignToken, rExp);
        SyntaxCaseDefinition minusOperation = new SyntaxCaseDefinition(rExp, "minusOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "-", b)),
                rExp, minusToken, rExp);
        SyntaxCaseDefinition plusOperation = new SyntaxCaseDefinition(rExp, "plusOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "+", b)),
                rExp, plusToken, rExp);
        SyntaxCaseDefinition intervalOperation = new SyntaxCaseDefinition(rExp, "intervalOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "..", b)),
                rExp, doubleDotToken, rExp);
        SyntaxCaseDefinition greaterOperation = new SyntaxCaseDefinition(rExp, "greaterOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, ">", b)),
                rExp, greaterOperatorToken, rExp);
        SyntaxCaseDefinition equalGreaterOperation = new SyntaxCaseDefinition(rExp, "equalGreaterOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, ">=", b)),
                rExp, equalGreaterOperatorToken, rExp);
        SyntaxCaseDefinition lessOperation = new SyntaxCaseDefinition(rExp, "lessOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "<", b)),
                rExp, lessOperatorToken, rExp);
        SyntaxCaseDefinition equalLessOperation = new SyntaxCaseDefinition(rExp, "equalLessOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "<=", b)),
                rExp, equalLessOperatorToken, rExp);
        SyntaxCaseDefinition isOperation = new SyntaxCaseDefinition(rExp, "isOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperatorReverse(a, "is", b)),
                rExp, isToken, rExp);
        SyntaxCaseDefinition equalsOperation = new SyntaxCaseDefinition(rExp, "equalsOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "==", b)),
                rExp, equalsOperatorToken, rExp);
        SyntaxCaseDefinition notEqualsOperation = new SyntaxCaseDefinition(rExp, "notEqualsOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "!=", b)),
                rExp, notEqualsOperatorToken, rExp);
        SyntaxCaseDefinition isIdenticalOperation = new SyntaxCaseDefinition(rExp, "isIdenticalOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "===", b)),
                rExp, sameInstanceOperatorToken, rExp);

        SyntaxCaseDefinition logicalAndOperation = new SyntaxCaseDefinition(rExp, "logicalAndOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "and", b)),
                rExp, andOperatorToken, rExp);
        SyntaxCaseDefinition logicalOrOperation = new SyntaxCaseDefinition(rExp, "logicalOrOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "or", b)),
                rExp, orOperatorToken, rExp);

        SyntaxCaseDefinition ifThenElseStatement = new SyntaxCaseDefinition(rExp, "ifThenElseStatement",
                (n, s) -> new IfThenElseStatement(s.convert(n.get(1)), s.convert(n.get(3)), s.convert(n.get(5))),
                ifToken, rExp, thenToken, rExp, elseToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition ifThenStatement = new SyntaxCaseDefinition(rExp, "ifThenStatement",
                (n, s) -> new IfThenStatement(s.convert(n.get(1)), s.convert(n.get(3))),
                ifToken, rExp, thenToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition whileStatement = new SyntaxCaseDefinition(rExp, "whileStatement",
                (n, s) -> new WhileStatement(s.convert(n.get(1)), s.convert(n.get(3))),
                whileToken, rExp, doToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition forInStatement = new SyntaxCaseDefinition(rExp, "forInStatement",
                (n, s) -> new ForInStatement(
                        ((Identifier) s.convert(n.get(1))),
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                forToken, ident, inToken, rExp, doToken, rExp);

        SyntaxCaseDefinition assignment = new SyntaxCaseDefinition(rExp, "assignment",
                (n, s) -> new Assignment(s.convert(n.get(0)), s.convert(n.get(2))),
                lExp, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition destructuralAssignment = new SyntaxCaseDefinition(rExp, "destructuralAssignment",
                (n, s) -> new DestructuralAssignment(s.convert(n.get(0)), s.convert(n.get(2))),
                lExp, destructuralAssignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);


        SyntaxCaseDefinition taggedExpression = new SyntaxCaseDefinition(rExp, "taggedExpression",
                (n, s) -> new TaggedExpression(s.convert(n.get(0)), s.convert(n.get(2))),
                ident, tagSetOperatorToken, rExp);

        SyntaxCaseDefinition rExpListBase = new SyntaxCaseDefinition(rExpList, "rExpListBase",
                new UBOConverterMethod<RValueList, RValue, RValue>(RValueList::new),
                rExp, commaToken, rExp);
        SyntaxCaseDefinition rExpListStep = new SyntaxCaseDefinition(rExpList, "rExpListStep",
                new UBOConverterMethod<RValueList, RValueList, RValue>(RValueList::new),
                rExpList, commaToken, rExp);
        SyntaxCaseDefinition parameterDefinitionListStep = new SyntaxCaseDefinition(paramList, "parameterDefinitionListStep",
                new UBOConverterMethod<ParameterDefinitionList, ParameterDefinitionList, ParameterDefinition>(ParameterDefinitionListImpl::new),
                paramList, commaToken, param);
        SyntaxCaseDefinition lExpListBase = new SyntaxCaseDefinition(lExpList, "lExpListBase",
                new UBOConverterMethod<LValueList, LValue, LValue>(LValueList::new),
                lExp, commaToken, lExp);
        SyntaxCaseDefinition lExpListStep = new SyntaxCaseDefinition(lExpList, "lExpListStep",
                new UBOConverterMethod<LValueList, LValueList, LValue>(LValueList::new),
                lExpList, commaToken, lExp);
        SyntaxCaseDefinition identifierListBase = new SyntaxCaseDefinition(identList, "identifierListBase",
                new UBOConverterMethod<IdentifierList, Identifier, Identifier>(IdentifierList::new),
                ident, commaToken, ident);
        SyntaxCaseDefinition identifierListStep = new SyntaxCaseDefinition(identList, "identifierListStep",
                new UBOConverterMethod<IdentifierList, IdentifierList, Identifier>(IdentifierList::new),
                identList, commaToken, ident);

        SyntaxCaseDefinition printOperation0 = new SyntaxCaseDefinition(rExp, "printOperation0",
                (n, s) -> (RValue) mem -> new PrintOperation(new ToolString(mem, "")).evaluate(mem),
                printOperatorToken);
        SyntaxCaseDefinition printOperation1 = new SyntaxCaseDefinition(rExp, "printOperation1",
                (n, s) -> new PrintOperation(s.convert(n.get(1))),
                printOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition printOperation2 = new SyntaxCaseDefinition(rExp, "printOperation2",
                (n, s) -> (RValue) mem -> new PrintOperation(((RValueList) s.convert(n.get(1))).generateToolTuple(mem))
                        .evaluate(mem),
                printOperatorToken, rExpList).parsingDirection(Associativity.RightToLeft);

        SyntaxCaseDefinition breakStatement1 = new SyntaxCaseDefinition(rExp, "breakStatement1",
                (n, s) -> new BreakStatement(s.convert(n.get(1))),
                breakToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition taggedBreakStatement1 = new SyntaxCaseDefinition(rExp, "taggedBreakStatement1",
                (n, s) -> new BreakStatement(s.convert(n.get(1)),
                        ((Identifier) s.convert(n.get(3))).getIdentifierString()),
                breakToken, rExp, tagDereferenceOperatorToken, ident).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition breakStatement2 = new SyntaxCaseDefinition(rExp, "breakStatement2",
                (n, s) -> (RValue) mem -> new BreakStatement(((RValueList) s.convert(n.get(1))).generateToolList(mem))
                        .evaluate(mem),
                breakToken, rExpList).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition taggedBreakStatement2 = new SyntaxCaseDefinition(rExp, "taggedBreakStatement2",
                (n, s) -> (RValue) mem -> new BreakStatement(
                        ((RValueList) s.convert(n.get(1))).generateToolList(mem),
                        ((Identifier) s.convert(n.get(3))).getIdentifierString()).evaluate(mem),
                breakToken, rExpList, tagDereferenceOperatorToken, ident).parsingDirection(Associativity.RightToLeft);

        SyntaxCaseDefinition sequentialComposition = new SyntaxCaseDefinition(rExp, "sequentialComposition",
                new CBOConverterMethod<RValue>(SequentialComposition::new),
                rExp, semicolonToken, rExp);

        SyntaxCaseDefinition methodDefinition0 = new SyntaxCaseDefinition(rExp, "methodDefinition0",
                (n, s) -> new DefinitionMethod(s.convert(n.get(1)), s.convert(n.get(5))),
                defToken, ident, openRoundBracketToken, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition methodDefinition1 = new SyntaxCaseDefinition(rExp, "methodDefinition1",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        Collections.singletonList(s.convert(n.get(3))),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, param, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition methodDefinition2 = new SyntaxCaseDefinition(rExp, "methodDefinition2",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        ((ParameterDefinitionList)s.convert(n.get(3))).getParameterDefinitions(),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, paramList, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition methodDefinitionEB0 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB0",
                (n, s) -> new DefinitionMethod(s.convert(n.get(1)), s.convert(n.get(5))),
                defToken, ident, openRoundBracketToken, closedRoundBracketToken, doToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition methodDefinitionEB1 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB1",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        Collections.singletonList(s.convert(n.get(3))),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, param, closedRoundBracketToken, doToken, rExp)
                .parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition methodDefinitionEB2 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB2",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        ((ParameterDefinitionList)s.convert(n.get(3))).getParameterDefinitions(),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, paramList, closedRoundBracketToken, doToken, rExp)
                .parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition getterDefinition = new SyntaxCaseDefinition(rExp, "getterDefinition",
                (n, s) -> new DefinitionGetter(((Identifier) s.convert(n.get(1))).getIdentifierString(), s.convert(n.get(3))),
                getterToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition getterDefinitionEB = new SyntaxCaseDefinition(rExp, "getterDefinitionEB",
                (n, s) -> new DefinitionGetter(((Identifier) s.convert(n.get(1))).getIdentifierString(), s.convert(n.get(3))),
                getterToken, ident, doToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition setterDefinition = new SyntaxCaseDefinition(rExp, "setterDefinition",
                (n, s) -> new DefinitionSetter(((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        memory.baseTypes().C_OBJECT, s.convert(n.get(3))),
                setterToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition setterDefinitionEB = new SyntaxCaseDefinition(rExp, "setterDefinitionEB",
                (n, s) -> new DefinitionSetter(((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        memory.baseTypes().C_OBJECT, s.convert(n.get(3))),
                setterToken, ident, doToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition ctorDefinition0 = new SyntaxCaseDefinition(rExp, "ctorDefinition0",
                (n, s) -> new DefinitionCtor(s.convert(n.get(2))),
                ctorToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition ctorDefinition1 = new SyntaxCaseDefinition(rExp, "ctorDefinition1",
                (n, s) -> new DefinitionCtor(Collections.singletonList((ParameterDefinition) s.convert(n.get(1))), s.convert(n.get(3))),
                ctorToken, param, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition ctorDefinition2 = new SyntaxCaseDefinition(rExp, "ctorDefinition2",
                (n, s) -> new DefinitionCtor(((ParameterDefinitionList) s.convert(n.get(1))).getParameterDefinitions(), s.convert(n.get(3))),
                ctorToken, paramList, openCurlyBracketToken, rExp, closedCurlyBracketToken);

        SyntaxCaseDefinition functorCallOperatorDefinition = new SyntaxCaseDefinition(rExp, "functorCallOperatorDefinition",
                (n, s) -> DefinitionOperator.binaryParametric(
                        s.convert(n.get(1)),
                        "()",
                        s.convert(n.get(3)),
                        s.convert(n.get(6))),
                operatorToken, rExp, openRoundBracketToken, rExp, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition elementAccessOperatorDefinition = new SyntaxCaseDefinition(rExp, "elementAccessOperatorDefinition",
                (n, s) -> DefinitionOperator.binaryParametric(
                        s.convert(n.get(1)),
                        "[]",
                        s.convert(n.get(3)),
                        s.convert(n.get(6))),
                operatorToken, rExp, openSquareBracketToken, rExp, closedSquareBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition typeAccessOperatorDefinition = new SyntaxCaseDefinition(rExp, "typeAccessOperatorDefinition",
                (n, s) -> DefinitionOperator.binaryParametric(
                        s.convert(n.get(1)),
                        "<>",
                        s.convert(n.get(3)),
                        s.convert(n.get(6))),
                operatorToken, rExp, lessOperatorToken, rExp, greaterOperatorToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition unaryMinusOperatorDefinition = new SyntaxCaseDefinition(rExp, "unaryMinusOperatorDefinition",
                (n, s) -> DefinitionOperator.unaryPrefix("-", s.convert(n.get(2)), s.convert(n.get(4))),
                operatorToken, minusToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalNotOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalNotOperatorDefinition",
                (n, s) -> DefinitionOperator.unaryPrefix("!", s.convert(n.get(2)), s.convert(n.get(4))),
                operatorToken, exclamationPointToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);

        SyntaxCaseDefinition intervalOperatorDefinition = new SyntaxCaseDefinition(rExp, "intervalOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "..",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, doubleDotToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition asteriskOperatorDefinition = new SyntaxCaseDefinition(rExp, "asteriskOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "*",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, asteriskToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition slashOperatorDefinition = new SyntaxCaseDefinition(rExp, "slashOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "/",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, slashToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition percentSignOperatorDefinition = new SyntaxCaseDefinition(rExp, "percentSignOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "%",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, percentSignToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition minusOperatorDefinition = new SyntaxCaseDefinition(rExp, "minusOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "-",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, minusToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition plusOperatorDefinition = new SyntaxCaseDefinition(rExp, "plusOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "+",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, plusToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition greaterOperatorDefinition = new SyntaxCaseDefinition(rExp, "greaterOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        ">",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, greaterOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalGreaterOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalGreaterOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        ">=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, equalGreaterOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition lessOperatorDefinition = new SyntaxCaseDefinition(rExp, "lessOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "<",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, lessOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalLessOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalLessOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "<=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, equalLessOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalsOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalsOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "==",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, equalsOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition notEqualsOperatorDefinition = new SyntaxCaseDefinition(rExp, "notEqualsOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "!=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, notEqualsOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalAndOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalAndOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "and",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, andOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalOrOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalOrOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "or",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, rExp, orOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition destructuralGetterDefinition = new SyntaxCaseDefinition(rExp, "destructuralGetterDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(2)),
                        ":=",
                        memory.baseTypes().C_INTEGER,
                        s.convert(n.get(4))),
                operatorToken, destructuralAssignmentOperatorToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);

        SyntaxCaseDefinition classDefinitionA = new SyntaxCaseDefinition(rExp, "classDefinitionA",
                (n, s) -> new DefinitionClass(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        new ArrayList<>(), s.convert(n.get(3))),
                classToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition classDefinitionB1 = new SyntaxCaseDefinition(rExp, "classDefinitionB1",
                (n, s) -> new DefinitionClass(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        Collections.singletonList(s.convert(n.get(3))),
                        s.convert(n.get(5))),
                classToken, ident, colonToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition classDefinitionB2 = new SyntaxCaseDefinition(rExp, "classDefinitionB2",
                (n, s) -> new DefinitionClass(
                        ((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        ((RValueList) s.convert(n.get(3))).getUnevaluatedList(),
                        s.convert(n.get(5))),
                classToken, ident, colonToken, rExpList, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition extensionDefinition = new SyntaxCaseDefinition(rExp, "extensionDefinition",
                (n, s) -> new DefinitionExtension(s.convert(n.get(1)), s.convert(n.get(3))),
                extensionToken, rExp, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition extensorDefinition = new SyntaxCaseDefinition(rExp, "extensorDefinition",
                (n, s) -> new DefinitionExtension(s.convert(n.get(1)), s.convert(n.get(3))),
                extensorToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition parameterDefinitionListBase = new SyntaxCaseDefinition(paramList, "parameterDefinitionListBase",
                new UBOConverterMethod<ParameterDefinitionList, ParameterDefinition, ParameterDefinition>(ParameterDefinitionListImpl::new),
                param, commaToken, param);



        SyntaxCaseDefinition[] grammar = new SyntaxCaseDefinition[]{
                nullLiteral, trueConst, falseConst, numeral, string,
                thisReference, identifier,
                expressionBetweenRoundBrackets, expressionBetweenCurlyBrackets,
                rValueListBetweenBrackets,
                lValueListBetweenBrackets1,
                lValueListBetweenBrackets2,
                parameterDeclaration,

                localCall0, localCall1, localCall2,
                localDotIdentifier,
                localDotCall0, localDotCall1, localDotCall2,
                localAtIdentifier,
                localAtCall0, localAtCall1, localAtCall2,
                objectDotIdentifier,
                objectDotCall0, objectDotCall1, objectDotCall2,
                objectAtIdentifier,
                objectAtCall0, objectAtCall1, objectAtCall2,

                localDefinitionVariable, valDefinitionProperty, varDefinitionProperty,

                arrayLiteral0, arrayLiteral1, arrayLiteral2,

                typeAccessOperation1, typeAccessOperation2,
                elementAccessOperation1, elementAccessOperation2,
                unaryMinusOperation,
                logicalNotOperation,
                asteriskOperation, slashOperation, percentSignOperation,
                minusOperation, plusOperation,
                intervalOperation,
                greaterOperation, equalGreaterOperation,
                lessOperation, equalLessOperation,
                isOperation,
                equalsOperation, notEqualsOperation,
                isIdenticalOperation,
                logicalAndOperation,
                logicalOrOperation,

                ifThenElseStatement, ifThenStatement,
                whileStatement,
                forInStatement,
                assignment, destructuralAssignment,

                taggedExpression,

                rExpListBase, rExpListStep,
                parameterDefinitionListBase, parameterDefinitionListStep,
                lExpListBase, lExpListStep,
                identifierListBase, identifierListStep,

                printOperation0, printOperation1, printOperation2,

                breakStatement1, taggedBreakStatement1,
                breakStatement2, taggedBreakStatement2,

                sequentialComposition,

                methodDefinition0, methodDefinition1, methodDefinition2,
                methodDefinitionEB0, methodDefinitionEB1, methodDefinitionEB2,
                getterDefinition, getterDefinitionEB, setterDefinition, setterDefinitionEB,
                ctorDefinition0, ctorDefinition1, ctorDefinition2,

                functorCallOperatorDefinition,
                elementAccessOperatorDefinition,
                typeAccessOperatorDefinition,
                unaryMinusOperatorDefinition,
                logicalNotOperatorDefinition,
                intervalOperatorDefinition,
                asteriskOperatorDefinition, slashOperatorDefinition, percentSignOperatorDefinition,
                minusOperatorDefinition, plusOperatorDefinition,
                greaterOperatorDefinition, equalGreaterOperatorDefinition,
                lessOperatorDefinition, equalLessOperatorDefinition,
                equalsOperatorDefinition, notEqualsOperatorDefinition,
                logicalAndOperatorDefinition,
                logicalOrOperatorDefinition,
                destructuralGetterDefinition,

                classDefinitionA, classDefinitionB1, classDefinitionB2,
                extensionDefinition, extensorDefinition,
        };
        return new Interpreter(lexicon, grammar);
    }

    private static List<TokenConverter> getConverters(TokenCategoryDefinition... tcds) {
        List<TokenConverter> result = new ArrayList<>();
        for (TokenCategoryDefinition tcd : tcds) {
            if(tcd.getConverter()!=null)result.add(tcd.getConverter());
        }
        return result;
    }
}
