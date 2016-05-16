package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.objects.basetypes.ToolBoolean;
import com.parsleyj.tool.objects.basetypes.ToolInteger;
import com.parsleyj.tool.objects.basetypes.ToolString;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.semantics.*;
import com.parsleyj.toolparser.parser.Associativity;
import com.parsleyj.toolparser.parser.SyntaxClass;
import com.parsleyj.toolparser.program.*;
import com.parsleyj.toolparser.semanticsconverter.CBOConverterMethod;
import com.parsleyj.toolparser.semanticsconverter.TokenConverter;
import com.parsleyj.toolparser.semanticsconverter.UBOConverterMethod;
import com.parsleyj.toolparser.tokenizer.TokenCategory;
import com.parsleyj.utils.PJ;
import com.parsleyj.utils.SimpleWrapConverterMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class TestMain {

    public static final boolean PRINT_DEBUG = false;
    public static final boolean PRINT_TOOL_EXCEPTION_STACK_TRACE = true;
    public static final boolean MULTILINE = false;
    public static final boolean PRINT_RESULTS = true;

    public static void test2() {
        Program.VERBOSE = PRINT_DEBUG;
        Scanner sc = new Scanner(System.in);
        String memName = "M";
        Memory m = new Memory(memName);
        m.pushScope();
        m.addObjectToHeap(BaseTypes.O_NULL);
        m.loadClasses(BaseTypes.getAllBaseClasses());
        ToolString testString = new ToolString("yay");
        m.addObjectToHeap(testString);
        BaseTypes.C_TOOL.addClassField(new Reference("test", BaseTypes.C_STRING, testString.getId()));
        ProgramGenerator pg = getDefaultInterpreter();
        pg.setPrintDebugMessages(PRINT_DEBUG);
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

            Program prog = null;
            try {
                prog = pg.generate("testParsed", programString, rExp, (p, c) -> {
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

    private static SyntaxClass rExp = new SyntaxClass("rExp");

    private static SyntaxClass lExp = new SyntaxClass("lExp", rExp); //lExp "extends" rExp = lExp can be treated as rExp
    private static SyntaxClass ident = new SyntaxClass("ident", lExp);

    private static SyntaxClass csel = new SyntaxClass("csel");

    private static ProgramGenerator getDefaultInterpreter() {
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
        TokenCategoryDefinition identifierToken = new TokenCategoryDefinition("IDENTIFIER", "[_a-zA-Z][_a-zA-Z0-9]*",
                Identifier::new);
        MultiPatternDefinition identifierMultiPattern = new MultiPatternDefinition("[_a-zA-Z][_a-zA-Z0-9]*") {
            @Override
            public List<TokenConverter> getDeclaredTokenConverters() {
                return getConverters(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken, toOperatorToken, andOperatorToken,
                        orOperatorToken, notOperatorToken, identifierToken);
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
                    default: return identifierToken;
                }
            }

            @Override
            public List<TokenCategory> declaredTokenCategories() {
                return Arrays.asList(nullToken, trueToken, falseToken, whileToken, forToken, inToken,
                        doToken, ifToken, thenToken, elseToken, toOperatorToken, andOperatorToken,
                        orOperatorToken, notOperatorToken, identifierToken);
            }
        };
        TokenCategoryDefinition dotToken = new TokenCategoryDefinition("DOT", "\\Q.\\E");
        TokenCategoryDefinition exclamationPointToken = new TokenCategoryDefinition("EXCLAMATION_POINT", "\\Q!\\E");
        TokenCategoryDefinition commaToken = new TokenCategoryDefinition("COMMA", "\\Q,\\E");
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
                dotToken, exclamationPointToken, commaToken,
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

        SyntaxCaseDefinition functionCall0 = new SyntaxCaseDefinition(rExp, "functionCall0",
                (n, s) -> new MethodCall(
                        ToolMethod.METHOD_CATEGORY_METHOD,
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{BaseTypes.C_TOOL},
                        new RValue[]{}),
                ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition functionCall1 = new SyntaxCaseDefinition(rExp, "functionCall1",
                (n, s) -> new MethodCall(
                        ToolMethod.METHOD_CATEGORY_METHOD,
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{BaseTypes.C_TOOL},
                        new RValue[]{s.convert(n.get(2))}),
                ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition functionCall2 = new SyntaxCaseDefinition(rExp, "functionCall2",
                (n, s) -> new MethodCall(
                        ToolMethod.METHOD_CATEGORY_METHOD,
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{BaseTypes.C_TOOL},
                        ((CommaSeparatedExpressionList) s.convert(n.get(2))).getUnevaluatedArray()),
                ident, openRoundBracketToken, csel, closedRoundBracketToken);

        SyntaxCaseDefinition dotNotationField = new SyntaxCaseDefinition(lExp, "dotNotationField",
                (n, s) -> new DotNotationField(s.convert(n.get(0)), s.convert(n.get(2))),
                rExp, dotToken, ident);
        SyntaxCaseDefinition dotNotationMethodCall0 = new SyntaxCaseDefinition(rExp, "dotNotationMethodCall0",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new MethodCall(
                            ToolMethod.METHOD_CATEGORY_METHOD,
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{r},
                            new RValue[]{});
                },
                rExp, dotToken, ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition dotNotationMethodCall1 = new SyntaxCaseDefinition(rExp, "dotNotationMethodCall1",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new MethodCall(
                            ToolMethod.METHOD_CATEGORY_METHOD,
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{r},
                            new RValue[]{s.convert(n.get(4))});
                },
                rExp, dotToken, ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition dotNotationMethodCall2 = new SyntaxCaseDefinition(rExp, "dotNotationMethodCall2",
                (n, s) -> {
                    RValue r = s.convert(n.get(0));
                    return new MethodCall(
                            ToolMethod.METHOD_CATEGORY_METHOD,
                            r,
                            ((Identifier) s.convert(n.get(2))).getIdentifierString(),
                            new RValue[]{r},
                            ((CommaSeparatedExpressionList) s.convert(n.get(4))).getUnevaluatedArray());
                },
                rExp, dotToken, ident, openRoundBracketToken, csel, closedRoundBracketToken);
        SyntaxCaseDefinition newVarDeclaration = new SyntaxCaseDefinition(lExp, "newVarDeclaration",
                (n, s) -> new NewVarDeclaration(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                dotToken, ident).parsingDirection(Associativity.RightToLeft);

        SyntaxCaseDefinition arrayLiteral = new SyntaxCaseDefinition(rExp, "arrayLiteral",
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
                (n, s) ->
                        MethodCall.prefixOperator("-", s.convert(n.get(1))),
                minusToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition logicalNotOperation = new SyntaxCaseDefinition(rExp, "logicalNotOperation",
                (n, s) ->
                        MethodCall.prefixOperator("!", s.convert(n.get(1))),
                exclamationPointToken, rExp).parsingDirection(Associativity.RightToLeft);
        SyntaxCaseDefinition intervalOperation = new SyntaxCaseDefinition(rExp, "intervalOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "to", b)),
                rExp, toOperatorToken, rExp);
        SyntaxCaseDefinition asteriskOperation = new SyntaxCaseDefinition(rExp, "asteriskOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "*", b)),
                rExp, asteriskToken, rExp);
        SyntaxCaseDefinition slashOperation = new SyntaxCaseDefinition(rExp, "slashOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "/", b)),
                rExp, slashToken, rExp);
        SyntaxCaseDefinition percentSignOperation = new SyntaxCaseDefinition(rExp, "percentSignOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "%", b)),
                rExp, percentSignToken, rExp);
        SyntaxCaseDefinition minusOperation = new SyntaxCaseDefinition(rExp, "minusOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "-", b)),
                rExp, minusToken, rExp);
        SyntaxCaseDefinition plusOperation = new SyntaxCaseDefinition(rExp, "plusOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "+", b)),
                rExp, plusToken, rExp);
        SyntaxCaseDefinition greaterOperation = new SyntaxCaseDefinition(rExp, "greaterOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, ">", b)),
                rExp, greaterOperatorToken, rExp);
        SyntaxCaseDefinition equalGreaterOperation = new SyntaxCaseDefinition(rExp, "equalGreaterOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, ">=", b)),
                rExp, equalGreaterOperatorToken, rExp);
        SyntaxCaseDefinition lessOperation = new SyntaxCaseDefinition(rExp, "lessOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "<", b)),
                rExp, lessOperatorToken, rExp);
        SyntaxCaseDefinition equalLessOperation = new SyntaxCaseDefinition(rExp, "equalLessOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "<=", b)),
                rExp, equalLessOperatorToken, rExp);
        SyntaxCaseDefinition equalsOperation = new SyntaxCaseDefinition(rExp, "equalsOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "==", b)),
                rExp, equalsOperatorToken, rExp);
        SyntaxCaseDefinition notEqualsOperation = new SyntaxCaseDefinition(rExp, "notEqualsOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "!=", b)),
                rExp, notEqualsOperatorToken, rExp);
        SyntaxCaseDefinition logicalAndOperation = new SyntaxCaseDefinition(rExp, "logicalAndOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "and", b)),
                rExp, andOperatorToken, rExp);
        SyntaxCaseDefinition logicalOrOperation = new SyntaxCaseDefinition(rExp, "logicalOrOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        MethodCall.binaryOperator(a, "or", b)),
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
                        s.convert(n.get(5))
                ),
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

        SyntaxCaseDefinition[] grammar = new SyntaxCaseDefinition[]{
                nullLiteral, trueConst, falseConst, numeral, string,
                identifier,
                expressionBetweenRoundBrackets,
                functionCall0, functionCall1, functionCall2,
                dotNotationField,
                dotNotationMethodCall0, dotNotationMethodCall1, dotNotationMethodCall2,
                newVarDeclaration,
                arrayLiteral,
                elementAccessOperation1,
                elementAccessOperation2,
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
                sequentialComposition
        };
        return new ProgramGenerator(lexicon, grammar);
    }

    private static List<TokenConverter> getConverters(TokenCategoryDefinition... tcds) {
        List<TokenConverter> result = new ArrayList<>();
        for (TokenCategoryDefinition tcd : tcds) {
            if(tcd.getConverter()!=null)result.add(tcd.getConverter());
        }
        return result;
    }

    public static void main(String[] args) {
        test2();
    }
}
