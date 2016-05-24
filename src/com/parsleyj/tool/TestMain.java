package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.semantics.base.*;
import com.parsleyj.tool.semantics.expr.Assignment;
import com.parsleyj.tool.semantics.expr.ElementAccessOperation;
import com.parsleyj.tool.semantics.expr.ExpressionBlock;
import com.parsleyj.tool.semantics.expr.ScopedBlock;
import com.parsleyj.tool.semantics.flowcontrol.*;
import com.parsleyj.tool.semantics.nametabled.*;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;
import com.parsleyj.tool.semantics.util.MethodCall;
import com.parsleyj.toolparser.parser.Associativity;
import com.parsleyj.toolparser.parser.SyntaxClass;
import com.parsleyj.toolparser.program.*;
import com.parsleyj.toolparser.semanticsconverter.CBOConverterMethod;
import com.parsleyj.toolparser.semanticsconverter.TokenConverter;
import com.parsleyj.toolparser.semanticsconverter.UBOConverterMethod;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.SimpleWrapConverterMethod;

import java.util.*;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class TestMain {

    public static final boolean PRINT_DEBUG = false;
    public static final boolean PRINT_TOOL_EXCEPTION_STACK_TRACE = false;
    public static final boolean MULTILINE = false;
    public static final boolean PRINT_RESULTS = true;

    public static void test() {
        Program.VERBOSE = PRINT_DEBUG;
        Scanner sc = new Scanner(System.in);
        String memName = "M";
        Memory m = new Memory(memName);
        m.pushCallFrame();
        m.pushScope();
        m.addObjectToHeap(BaseTypes.O_NULL);
        m.loadClasses(BaseTypes.getAllBaseClasses());
        Interpreter interp = getDefaultInterpreter();
        interp.setPrintDebugMessages(PRINT_DEBUG);
        while (true) {
            StringBuilder sb = new StringBuilder();
            if (MULTILINE) {
                while (true) {
                    String l = sc.nextLine();
                    if (l.endsWith("#")) {
                        sb.append(l.substring(0, l.length() - 1));
                        break;
                    }
                    sb.append(l);
                }
            } else {
                sb.append(sc.nextLine());
            }
            String programString = sb.toString();

            if (programString.equals("exit")) break;

            try {
                Program prog = interp.interpret("testParsed", programString, rExp, (p, c) -> { //TODO SET AGAIN TO rExp
                    RValue e = (RValue) p.getRootSemanticObject();
                    try {
                        ToolObject to = e.evaluate((Memory) c.getConfigurationElement(memName));
                        if (PRINT_RESULTS) System.out.println("RESULT = " + to);
                    } catch (ToolNativeException e1) {
                        if (PRINT_TOOL_EXCEPTION_STACK_TRACE) e1.printStackTrace();
                        System.err.println("Tool Exception not handled of type " + e1.getExceptionObject().getBelongingClass().getClassName() + ": " + e1.getExceptionObject().getExplain());
                    }
                    return true;
                });
                prog.executeProgram(m);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static SyntaxClass param = new SyntaxClass("param");
    private static SyntaxClass paramlist = new SyntaxClass("paramlist");

    private static SyntaxClass rExp = new SyntaxClass("rExp");
    private static SyntaxClass lExp = new SyntaxClass("lExp", rExp); //lExp "extends" rExp = lExp can be treated as rExp
    private static SyntaxClass ident = new SyntaxClass("ident", lExp, param);


    private static SyntaxClass csel = new SyntaxClass("csel");

    private static Interpreter getDefaultInterpreter() {
        TokenCategoryDefinition stringToken = new TokenCategoryDefinition("STRING", "([\"'])(?:(?=(\\\\?))\\2.)*?\\1",
                ToolString::newFromLiteral);
        TokenCategoryDefinition nullToken = new TokenCategoryDefinition("NULL_KEYWORD", "\\Qnull\\E",
                (g) -> BaseTypes.O_NULL);
        TokenCategoryDefinition trueToken = new TokenCategoryDefinition("TRUE_KEYWORD", "\\Qtrue\\E",
                (g) -> new ToolBoolean(true));
        TokenCategoryDefinition falseToken = new TokenCategoryDefinition("FALSE_KEYWORD", "\\Qfalse\\E",
                (g) -> new ToolBoolean(false));
        TokenCategoryDefinition whileToken = new TokenCategoryDefinition("WHILE_KEYWORD", "\\Qwhile\\E");
        TokenCategoryDefinition forToken = new TokenCategoryDefinition("FOR_KEYWORD", "\\Qfor\\E");
        TokenCategoryDefinition inToken = new TokenCategoryDefinition("IN_KEYWORD", "\\Qin\\E");
        TokenCategoryDefinition doToken = new TokenCategoryDefinition("DO_KEYWORD", "\\Qdo\\E");
        TokenCategoryDefinition ifToken = new TokenCategoryDefinition("IF_KEYWORD", "\\Qif\\E");
        TokenCategoryDefinition thenToken = new TokenCategoryDefinition("THEN_KEYWORD", "\\Qthen\\E");
        TokenCategoryDefinition elseToken = new TokenCategoryDefinition("ELSE_KEYWORD", "\\Qelse\\E");
        TokenCategoryDefinition toOperatorToken = new TokenCategoryDefinition("TO_OPERATOR", "\\Qto\\E");
        TokenCategoryDefinition andOperatorToken = new TokenCategoryDefinition("AND_OPERATOR", "\\Qand\\E");
        TokenCategoryDefinition orOperatorToken = new TokenCategoryDefinition("OR_OPERATOR", "\\Qor\\E");
        TokenCategoryDefinition notOperatorToken = new TokenCategoryDefinition("NOT_OPERATOR", "\\Qnot\\E");
        TokenCategoryDefinition defToken = new TokenCategoryDefinition("DEF_KEYWORD", "\\Qdef\\E");
        TokenCategoryDefinition localToken = new TokenCategoryDefinition("LOCAL_KEYWORD", "\\Qlocal\\E");
        TokenCategoryDefinition getterToken = new TokenCategoryDefinition("GETTER_KEYWORD", "\\Qgetter\\E");
        TokenCategoryDefinition setterToken = new TokenCategoryDefinition("SETTER_KEYWORD", "\\Qsetter\\E");
        TokenCategoryDefinition classToken = new TokenCategoryDefinition("CLASS_KEYWORD", "\\Qclass\\E");
        TokenCategoryDefinition operatorToken = new TokenCategoryDefinition("OPERATOR_KEYWORD", "\\Qoperator\\E");
        TokenCategoryDefinition identifierToken = new TokenCategoryDefinition("IDENTIFIER", "[_a-zA-Z][_a-zA-Z0-9]*",
                LocalIdentifier::new);
        MultiPatternDefinition identifierMultiPattern = new MultiPatternDefinition("[_a-zA-Z][_a-zA-Z0-9]*") {
            @Override
            public List<TokenConverter> getDeclaredTokenConverters() {
                return getConverters(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken, toOperatorToken, andOperatorToken,
                        orOperatorToken, notOperatorToken,
                        defToken, localToken, getterToken, setterToken, operatorToken,
                        classToken,
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
                    case "to": return toOperatorToken;
                    case "and": return andOperatorToken;
                    case "or": return orOperatorToken;
                    case "not": return notOperatorToken;
                    case "def": return defToken;
                    case "local": return localToken;
                    case "getter": return getterToken;
                    case "setter": return setterToken;
                    case "class": return classToken;
                    case "operator": return operatorToken;
                    default: return identifierToken;
                }
            }

            @Override
            public List<TokenCategory> declaredTokenCategories() {
                return Arrays.asList(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken, toOperatorToken, andOperatorToken,
                        orOperatorToken, notOperatorToken,
                        defToken, localToken, getterToken, setterToken, operatorToken,
                        classToken,
                        identifierToken);
            }
        };
        TokenCategoryDefinition atToken = new TokenCategoryDefinition("AT", "\\Q@\\E");
        TokenCategoryDefinition dotToken = new TokenCategoryDefinition("DOT", "\\Q.\\E");
        TokenCategoryDefinition colonToken = new TokenCategoryDefinition("COLON", "\\Q:\\E");
        TokenCategoryDefinition commaToken = new TokenCategoryDefinition("COMMA", "\\Q,\\E");
        TokenCategoryDefinition exclamationPointToken = new TokenCategoryDefinition("EXCLAMATION_POINT", "\\Q!\\E");
        TokenCategoryDefinition plusToken = new TokenCategoryDefinition("PLUS", "\\Q+\\E");
        TokenCategoryDefinition minusToken = new TokenCategoryDefinition("MINUS", "\\Q-\\E");
        TokenCategoryDefinition asteriskToken = new TokenCategoryDefinition("ASTERISK", "\\Q*\\E");
        TokenCategoryDefinition slashToken = new TokenCategoryDefinition("SLASH", "\\Q/\\E");
        TokenCategoryDefinition percentSignToken = new TokenCategoryDefinition("PERCENT_SIGN", "\\Q%\\E");
        TokenCategoryDefinition getBlockDefinitionOperatorToken = new TokenCategoryDefinition("GET_BLOCK_DEFINITION_OPERATOR", "\\Q&\\E");
        TokenCategoryDefinition assignmentOperatorToken = new TokenCategoryDefinition("ASSIGNMENT_OPERATOR", "\\Q=\\E");
        TokenCategoryDefinition equalsOperatorToken = new TokenCategoryDefinition("EQUALS_OPERATOR", "\\Q==\\E");
        TokenCategoryDefinition notEqualsOperatorToken = new TokenCategoryDefinition("NOT_EQUALS_OPERATOR", "\\Q!=\\E");
        TokenCategoryDefinition greaterOperatorToken = new TokenCategoryDefinition("GREATER_OPERATOR", "\\Q>\\E");
        TokenCategoryDefinition equalGreaterOperatorToken = new TokenCategoryDefinition("EQUAL_GREATER_OPERATOR", "\\Q>=\\E");
        TokenCategoryDefinition lessOperatorToken = new TokenCategoryDefinition("LESS_OPERATOR", "\\Q<\\E");
        TokenCategoryDefinition equalLessOperatorToken = new TokenCategoryDefinition("EQUAL_LESS_OPERATOR", "\\Q<=\\E");
        TokenCategoryDefinition semicolonToken = new TokenCategoryDefinition("SEMICOLON", "\\Q;\\E");
        TokenCategoryDefinition openRoundBracketToken = new TokenCategoryDefinition("OPEN_ROUND_BRACKET", "\\Q(\\E");
        TokenCategoryDefinition closedRoundBracketToken = new TokenCategoryDefinition("CLOSED_ROUND_BRACKET", "\\Q)\\E");
        TokenCategoryDefinition openCurlyBracketToken = new TokenCategoryDefinition("OPEN_CURLY_BRACKET", "\\Q{\\E");
        TokenCategoryDefinition closedCurlyBracketToken = new TokenCategoryDefinition("CLOSED_CURLY_BRACKET", "\\Q}\\E");
        TokenCategoryDefinition openSquareBracketToken = new TokenCategoryDefinition("OPEN_SQUARE_BRACKET", "\\Q[\\E");
        TokenCategoryDefinition closedSquareBracketToken = new TokenCategoryDefinition("CLOSED_SQUARE_BRACKET", "\\Q]\\E");
        TokenCategoryDefinition numeralToken = new TokenCategoryDefinition("NUMERAL", "(0|([1-9]\\d*))",// "(?<=\\s|^)[-+]?\\d+(?=\\s|$)"
                g -> new ToolInteger(Integer.decode(g)));
        TokenCategoryDefinition blankToken = new TokenCategoryDefinition("BLANK", " ", true);
        TokenCategoryDefinition newLineToken = new TokenCategoryDefinition("NEWLINE", "\\Q\n\\E", true);

        LexicalPatternDefinition[] lexicon = new LexicalPatternDefinition[]{
                stringToken,
                identifierMultiPattern,
                atToken, dotToken, colonToken, commaToken,
                exclamationPointToken,
                plusToken, minusToken, asteriskToken, slashToken, percentSignToken,
                getBlockDefinitionOperatorToken,
                equalsOperatorToken, notEqualsOperatorToken,
                greaterOperatorToken, equalGreaterOperatorToken,
                lessOperatorToken, equalLessOperatorToken,
                assignmentOperatorToken, semicolonToken,
                openRoundBracketToken, closedRoundBracketToken,
                openCurlyBracketToken, closedCurlyBracketToken,
                openSquareBracketToken, closedSquareBracketToken,
                numeralToken,
                blankToken, newLineToken
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
                ident, colonToken, ident);

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
                        ((CommaSeparatedExpressionList) s.convert(n.get(2))).getUnevaluatedArray()),
                ident, openRoundBracketToken, csel, closedRoundBracketToken);

        SyntaxCaseDefinition objectDotIdentifier = new SyntaxCaseDefinition(lExp, "objectDotIdentifier",
                (n, s) -> new ObjectDotIdentifier(s.convert(n.get(0)), s.convert(n.get(2))),
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
                            ((CommaSeparatedExpressionList) s.convert(n.get(4))).getUnevaluatedArray());
                },
                rExp, dotToken, ident, openRoundBracketToken, csel, closedRoundBracketToken);
        SyntaxCaseDefinition localDefinitionVariable = new SyntaxCaseDefinition(lExp, "localDefinitionVariable",
                (n, s) -> new DefinitionVariable(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                localToken, ident).parsingDirection(Associativity.RightToLeft);

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
                            ((CommaSeparatedExpressionList) s.convert(n.get(4))).getUnevaluatedArray());
                },
                rExp, atToken, ident, openRoundBracketToken, csel, closedRoundBracketToken);

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
                        ((CommaSeparatedExpressionList) s.convert(n.get(3))).getUnevaluatedArray()),
                dotToken, ident, openRoundBracketToken, csel, closedRoundBracketToken);

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
                        ((CommaSeparatedExpressionList) s.convert(n.get(3))).getUnevaluatedArray()),
                atToken, ident, openRoundBracketToken, csel, closedRoundBracketToken);

        SyntaxCaseDefinition arrayLiteral0 = new SyntaxCaseDefinition(rExp, "arrayLiteral0",
                (n, s) -> (RValue) m -> new ToolList(new ArrayList<>()),
                openSquareBracketToken, closedSquareBracketToken);
        SyntaxCaseDefinition arrayLiteral1 = new SyntaxCaseDefinition(rExp, "arrayLiteral1",
                (n, s) -> (RValue) m -> new ToolList(new ArrayList<>(Collections.singletonList(((RValue)s.convert(n.get(1))).evaluate(m)))),
                openSquareBracketToken, rExp, closedSquareBracketToken);
        SyntaxCaseDefinition arrayLiteral2 = new SyntaxCaseDefinition(rExp, "arrayLiteral2",
                (n, s) -> (RValue) m -> {
                    CommaSeparatedExpressionList cselist = s.convert(n.get(1));
                    return cselist.generateToolList(m);
                },
                openSquareBracketToken, csel, closedSquareBracketToken);
        SyntaxCaseDefinition elementAccessOperation1 = new SyntaxCaseDefinition(rExp, "elementAccessOperation1",
                (n, s) -> new ElementAccessOperation(s.convert(n.get(0)), s.convert(n.get(2)), true),
                rExp, openSquareBracketToken, rExp, closedSquareBracketToken);
        SyntaxCaseDefinition elementAccessOperation2 = new SyntaxCaseDefinition(rExp, "elementAccessOperation2",
                (n, s) -> new ElementAccessOperation(s.convert(n.get(0)), s.convert(n.get(2))),
                rExp, openSquareBracketToken, csel, closedSquareBracketToken);

        SyntaxCaseDefinition unaryMinusOperation = new SyntaxCaseDefinition(rExp, "unaryMinus",
                (n, s) -> MethodCall.prefixOperator("-", s.convert(n.get(1))),
                minusToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition logicalNotOperation = new SyntaxCaseDefinition(rExp, "logicalNotOperation",
                (n, s) -> MethodCall.prefixOperator("!", s.convert(n.get(1))),
                exclamationPointToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition intervalOperation = new SyntaxCaseDefinition(rExp, "intervalOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "to", b)),
                rExp, toOperatorToken, rExp);
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
        SyntaxCaseDefinition equalsOperation = new SyntaxCaseDefinition(rExp, "equalsOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "==", b)),
                rExp, equalsOperatorToken, rExp);
        SyntaxCaseDefinition notEqualsOperation = new SyntaxCaseDefinition(rExp, "notEqualsOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "!=", b)),
                rExp, notEqualsOperatorToken, rExp);
        SyntaxCaseDefinition logicalAndOperation = new SyntaxCaseDefinition(rExp, "logicalAndOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "and", b)),
                rExp, andOperatorToken, rExp);
        SyntaxCaseDefinition logicalOrOperation = new SyntaxCaseDefinition(rExp, "logicalOrOperation",
                new CBOConverterMethod<RValue>((a, b) -> MethodCall.binaryOperator(a, "or", b)),
                rExp, orOperatorToken, rExp);

        SyntaxCaseDefinition assignment = new SyntaxCaseDefinition(rExp, "assignment",
                (n, s) -> new Assignment(s.convert(n.get(0)), s.convert(n.get(2))),
                lExp, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);

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
        SyntaxCaseDefinition commaSeparatedExpressionListBase = new SyntaxCaseDefinition(csel, "commaSeparatedExpressionListBase",
                new UBOConverterMethod<CommaSeparatedExpressionList, RValue, RValue>(CommaSeparatedExpressionList::new),
                rExp, commaToken, rExp);
        SyntaxCaseDefinition commaSeparatedExpressionListStep = new SyntaxCaseDefinition(csel, "commaSeparatedExpressionListStep",
                new UBOConverterMethod<CommaSeparatedExpressionList, CommaSeparatedExpressionList, RValue>(CommaSeparatedExpressionList::new),
                csel, commaToken, rExp);
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
                defToken, ident, openRoundBracketToken, paramlist, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition methodDefinitionEB0 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB0",
                (n, s) -> new DefinitionMethod(s.convert(n.get(1)), s.convert(n.get(5))),
                defToken, ident, openRoundBracketToken, closedRoundBracketToken, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition methodDefinitionEB1 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB1",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        Collections.singletonList(s.convert(n.get(3))),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, param, closedRoundBracketToken, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition methodDefinitionEB2 = new SyntaxCaseDefinition(rExp, "methodDefinitionEB2",
                (n, s) -> new DefinitionMethod(
                        s.convert(n.get(1)),
                        ((ParameterDefinitionList)s.convert(n.get(3))).getParameterDefinitions(),
                        s.convert(n.get(6))),
                defToken, ident, openRoundBracketToken, paramlist, closedRoundBracketToken, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition getterDefinition = new SyntaxCaseDefinition(rExp, "getterDefinition",
                (n, s) -> new DefinitionGetter(((Identifier) s.convert(n.get(1))).getIdentifierString(), s.convert(n.get(3))),
                getterToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition getterDefinitionEB = new SyntaxCaseDefinition(rExp, "getterDefinitionEB",
                (n, s) -> new DefinitionGetter(((Identifier) s.convert(n.get(1))).getIdentifierString(), s.convert(n.get(3))),
                getterToken, ident, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition setterDefinition = new SyntaxCaseDefinition(rExp, "setterDefinition",
                (n, s) -> new DefinitionSetter(((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        BaseTypes.C_OBJECT, s.convert(n.get(3))),
                setterToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition setterDefinitionEB = new SyntaxCaseDefinition(rExp, "setterDefinitionEB",
                (n, s) -> new DefinitionSetter(((Identifier) s.convert(n.get(1))).getIdentifierString(),
                        BaseTypes.C_OBJECT, s.convert(n.get(3))),
                setterToken, ident, assignmentOperatorToken, rExp).parsingDirection(Associativity.RightToLeft);

        SyntaxCaseDefinition functorCallOperatorDefinition = new SyntaxCaseDefinition(rExp, "functorCallOperatorDefinition",
                (n, s) -> DefinitionOperator.binaryParametric(
                        s.convert(n.get(1)),
                        "()",
                        s.convert(n.get(3)),
                        s.convert(n.get(6))),
                operatorToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition elementAccessOperatorDefinition = new SyntaxCaseDefinition(rExp, "elementAccessOperatorDefinition",
                (n, s) -> DefinitionOperator.binaryParametric(
                        s.convert(n.get(1)),
                        "[]",
                        s.convert(n.get(3)),
                        s.convert(n.get(6))),
                operatorToken, ident, openSquareBracketToken, ident, closedSquareBracketToken, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition unaryMinusOperatorDefinition = new SyntaxCaseDefinition(rExp, "unaryMinusOperatorDefinition",
                (n, s) -> DefinitionOperator.unaryPrefix("-", s.convert(n.get(2)), s.convert(n.get(4))),
                operatorToken, minusToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalNotOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalNotOperatorDefinition",
                (n, s) -> DefinitionOperator.unaryPrefix("!", s.convert(n.get(2)), s.convert(n.get(4))),
                operatorToken, exclamationPointToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition intervalOperatorDefinition = new SyntaxCaseDefinition(rExp, "intervalOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "to",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, toOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition asteriskOperatorDefinition = new SyntaxCaseDefinition(rExp, "asteriskOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "*",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, asteriskToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition slashOperatorDefinition = new SyntaxCaseDefinition(rExp, "slashOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "/",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, slashToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition percentSignOperatorDefinition = new SyntaxCaseDefinition(rExp, "percentSignOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "%",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, percentSignToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition minusOperatorDefinition = new SyntaxCaseDefinition(rExp, "minusOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "-",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, minusToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition plusOperatorDefinition = new SyntaxCaseDefinition(rExp, "plusOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "+",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, plusToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition greaterOperatorDefinition = new SyntaxCaseDefinition(rExp, "greaterOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        ">",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, greaterOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalGreaterOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalGreaterOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        ">=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, equalGreaterOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition lessOperatorDefinition = new SyntaxCaseDefinition(rExp, "lessOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "<",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, lessOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalLessOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalLessOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "<=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, equalLessOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition equalsOperatorDefinition = new SyntaxCaseDefinition(rExp, "equalsOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "==",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, equalsOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition notEqualsOperatorDefinition = new SyntaxCaseDefinition(rExp, "notEqualsOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "!=",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, notEqualsOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalAndOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalAndOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "and",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, andOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);
        SyntaxCaseDefinition logicalOrOperatorDefinition = new SyntaxCaseDefinition(rExp, "logicalOrOperatorDefinition",
                (n, s) -> DefinitionOperator.binary(
                        s.convert(n.get(1)),
                        "or",
                        s.convert(n.get(3)),
                        s.convert(n.get(5))),
                operatorToken, ident, orOperatorToken, ident, openCurlyBracketToken, rExp, closedCurlyBracketToken);

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
                        ((CommaSeparatedExpressionList) s.convert(n.get(3))).getUnevaluatedList(),
                        s.convert(n.get(5))),
                classToken, ident, colonToken, csel, openCurlyBracketToken, rExp, closedCurlyBracketToken);

        SyntaxCaseDefinition parameterDefinitionListBase = new SyntaxCaseDefinition(paramlist, "parameterDefinitionListBase",
                new UBOConverterMethod<ParameterDefinitionList, ParameterDefinition, ParameterDefinition>(ParameterDefinitionList::new),
                param, commaToken, param);
        SyntaxCaseDefinition parameterDefinitionListStep = new SyntaxCaseDefinition(paramlist, "parameterDefinitionListStep",
                new UBOConverterMethod<ParameterDefinitionList, ParameterDefinitionList, ParameterDefinition>(ParameterDefinitionList::new),
                paramlist, commaToken, param);

        SyntaxCaseDefinition[] grammar = new SyntaxCaseDefinition[]{
                nullLiteral, trueConst, falseConst, numeral, string,
                identifier,
                expressionBetweenRoundBrackets, expressionBetweenCurlyBrackets,
                parameterDeclaration,

                localCall0, localCall1, localCall2,
                objectDotIdentifier,
                objectDotCall0, objectDotCall1, objectDotCall2,
                objectAtIdentifier,
                objectAtCall0, objectAtCall1, objectAtCall2,
                localDotIdentifier,
                localDotCall0, localDotCall1, localDotCall2,
                localAtIdentifier,
                localAtCall0, localAtCall1, localAtCall2,
                localDefinitionVariable,

                arrayLiteral0, arrayLiteral1, arrayLiteral2,

                elementAccessOperation1, elementAccessOperation2,
                unaryMinusOperation,
                logicalNotOperation,
                intervalOperation,
                asteriskOperation, slashOperation, percentSignOperation,
                minusOperation, plusOperation,
                greaterOperation, equalGreaterOperation,
                lessOperation, equalLessOperation,
                equalsOperation, notEqualsOperation,
                logicalAndOperation,
                logicalOrOperation,

                ifThenElseStatement, ifThenStatement,
                whileStatement,
                forInStatement,
                assignment,

                commaSeparatedExpressionListBase,
                commaSeparatedExpressionListStep,

                sequentialComposition,

                methodDefinition0, methodDefinition1, methodDefinition2,
                methodDefinitionEB0, methodDefinitionEB1, methodDefinitionEB2,
                getterDefinition, getterDefinitionEB, setterDefinition, setterDefinitionEB,

                functorCallOperatorDefinition,
                elementAccessOperatorDefinition,
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

                classDefinitionA, classDefinitionB1, classDefinitionB2,
                parameterDefinitionListBase,
                parameterDefinitionListStep,


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

    public static void main(String[] args) {
        test();
    }
}
