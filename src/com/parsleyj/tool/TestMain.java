package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolInternalException;
import com.parsleyj.toolparser.configuration.Configuration;
import com.parsleyj.toolparser.parser.SyntaxCase;
import com.parsleyj.toolparser.parser.SyntaxClass;
import com.parsleyj.toolparser.program.Program;
import com.parsleyj.toolparser.program.ProgramGenerator;
import com.parsleyj.toolparser.program.SyntaxCaseDefinition;
import com.parsleyj.toolparser.program.TokenCategoryDefinition;
import com.parsleyj.toolparser.semanticsconverter.CBOConverterMethod;
import com.parsleyj.toolparser.semanticsconverter.UBOConverterMethod;
import com.parsleyj.utils.SimpleWrapConverterMethod;

import java.util.Scanner;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class TestMain {






    public static void test1() {
        String memName = "DefaultMemory";
        Memory m = new Memory(memName);
        m.pushScope();

        Program p = new Program("testNonParsed",
                new IfThenElseStatement(new False(),
                        new ToolInteger(1),
                        new IfThenStatement(new True(),
                                new ToolInteger(2)))) {
            @Override
            public boolean execute(Configuration configuration) {
                RValue e = (RValue) this.getRootSemanticObject();
                try {
                    ToolObject to = e.evaluate((Memory) configuration.getConfigurationElement(memName));
                    if (PRINT_RESULTS) System.out.println("RESULT = " + to);
                } catch (ToolInternalException e1) {
                    e1.printStackTrace();
                    System.err.println("exception not handled of type " + e1.getExceptionObject().getBelongingClass() + ": " + e1.getExceptionObject().getExplain());
                }
                return true;
            }
        };

        p.executeProgram(m);
    }

    public static final boolean PRINT_DEBUG = true;
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
        BaseTypes.C_TOOL.addReferenceMember(new Reference("test", testString.getId()));
        ProgramGenerator pg = getDefaultInterpreterMini();
        pg.setPrintDebugMessages(PRINT_DEBUG);
        while (true) {
            StringBuilder sb = new StringBuilder();
            if(MULTILINE) {
                while (true) {
                    String l = sc.nextLine();
                    if (l.endsWith("#")) {
                        sb.append(l.substring(0, l.length() - 1));
                        break;
                    }
                    sb.append(l);
                }
            }else{
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
                    } catch (ToolInternalException e1) {
                        if (PRINT_DEBUG) e1.printStackTrace();
                        System.err.println("Tool Exception not handled of type " + e1.getExceptionObject().getBelongingClass().getClassName() + ": " + e1.getExceptionObject().getExplain());
                    }
                    return true;
                });
                prog.executeProgram(m);
            } catch (Throwable t){
                t.printStackTrace();
            }
        }
    }

    private static SyntaxClass rExp = new SyntaxClass("rExp");

    private static SyntaxClass lExp = new SyntaxClass("lExp", rExp); //lExp "extends" rExp
    private static SyntaxClass ident = new SyntaxClass("ident", lExp);
    private static SyntaxClass csel = new SyntaxClass("csel");

    private static ProgramGenerator getDefaultInterpreterMini() {
        TokenCategoryDefinition stringToken = new TokenCategoryDefinition("STRING", "([\"'])(?:(?=(\\\\?))\\2.)*?\\1",
                ToolString::newFromLiteral);
        TokenCategoryDefinition nullToken = new TokenCategoryDefinition("NULL_KEYWORD", "\\Qnull\\E",
                (g) -> BaseTypes.O_NULL);
        TokenCategoryDefinition trueToken = new TokenCategoryDefinition("TRUE_KEYWORD", "\\Qtrue\\E",
                (g) -> new True());
        TokenCategoryDefinition falseToken = new TokenCategoryDefinition("FALSE_KEYWORD", "\\Qfalse\\E",
                (g) -> new False());
        TokenCategoryDefinition whileToken = new TokenCategoryDefinition("WHILE_KEYWORD", "\\Qwhile\\E");
        TokenCategoryDefinition doToken = new TokenCategoryDefinition("DO_KEYWORD", "\\Qdo\\E");
        TokenCategoryDefinition ifToken = new TokenCategoryDefinition("IF_KEYWORD", "\\Qif\\E");
        TokenCategoryDefinition thenToken = new TokenCategoryDefinition("THEN_KEYWORD", "\\Qthen\\E");
        TokenCategoryDefinition elseToken = new TokenCategoryDefinition("ELSE_KEYWORD", "\\Qelse\\E");
        TokenCategoryDefinition andOperatorToken = new TokenCategoryDefinition("AND_OPERATOR", "\\Qand\\E");
        TokenCategoryDefinition orOperatorToken = new TokenCategoryDefinition("OR_OPERATOR", "\\Qor\\E");
        TokenCategoryDefinition notOperatorToken = new TokenCategoryDefinition("NOT_OPERATOR", "\\Qnot\\E");
        TokenCategoryDefinition identifierToken = new TokenCategoryDefinition("IDENTIFIER", "[_a-zA-Z][_a-zA-Z0-9]*",
                Identifier::new);
        TokenCategoryDefinition dotToken = new TokenCategoryDefinition("DOT", "\\Q.\\E");
        TokenCategoryDefinition commaToken = new TokenCategoryDefinition("COMMA", "\\Q,\\E");
        TokenCategoryDefinition plusToken = new TokenCategoryDefinition("PLUS", "\\Q+\\E");
        TokenCategoryDefinition minusToken = new TokenCategoryDefinition("MINUS", "\\Q-\\E");
        TokenCategoryDefinition asteriskToken = new TokenCategoryDefinition("ASTERISK", "\\Q*\\E");
        TokenCategoryDefinition slashToken = new TokenCategoryDefinition("SLASH", "\\Q/\\E");
        TokenCategoryDefinition percentSignToken = new TokenCategoryDefinition("PERCENT_SIGN", "\\Q%\\E");
        TokenCategoryDefinition getBlockDefinitionOperatorToken = new TokenCategoryDefinition("GET_BLOCK_DEFINITION_OPERATOR", "\\Q&\\E");
        TokenCategoryDefinition assignmentOperatorToken = new TokenCategoryDefinition("ASSIGNMENT_OPERATOR", "\\Q=\\E");
        TokenCategoryDefinition equalsOperatorToken = new TokenCategoryDefinition("EQUALS_OPERATOR", "\\Q==\\E");
        TokenCategoryDefinition greaterOperatorToken = new TokenCategoryDefinition("GREATER_OPERATOR", "\\Q>\\E");
        TokenCategoryDefinition lessOperatorToken = new TokenCategoryDefinition("LESS_OPERATOR", "\\Q<\\E");
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

        TokenCategoryDefinition[] lexicon = new TokenCategoryDefinition[]{
                stringToken,
                nullToken, trueToken, falseToken, whileToken, doToken, ifToken, thenToken, elseToken,
                andOperatorToken, orOperatorToken, notOperatorToken,
                identifierToken, dotToken, commaToken,
                plusToken, minusToken, asteriskToken, slashToken, percentSignToken,
                getBlockDefinitionOperatorToken,
                equalsOperatorToken, greaterOperatorToken, lessOperatorToken,
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
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{s.convert(n.get(2))}),
                ident, openRoundBracketToken, closedRoundBracketToken);
        SyntaxCaseDefinition functionCall1 = new SyntaxCaseDefinition(rExp, "functionCall1",
                (n, s) -> new MethodCall(
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        new RValue[]{s.convert(n.get(2))}),
                ident, openRoundBracketToken, rExp, closedRoundBracketToken);
        SyntaxCaseDefinition functionCall2 = new SyntaxCaseDefinition(rExp, "functionCall2",
                (n, s) -> new MethodCall(
                        BaseTypes.C_TOOL,
                        ((Identifier) s.convert(n.get(0))).getIdentifierString(),
                        ((CommaSeparatedExpressionList) s.convert(n.get(2))).getUnevaluatedArray()),
                ident, openRoundBracketToken, csel, closedRoundBracketToken);
        SyntaxCaseDefinition newVarDeclaration = new SyntaxCaseDefinition(lExp, "newVarDeclaration",
                (n, s) -> new NewVarDeclaration(((Identifier) s.convert(n.get(1))).getIdentifierString()),
                dotToken, ident).parsingDirection(SyntaxCase.ParsingDirection.RightToLeft);
        SyntaxCaseDefinition dotNotationField = new SyntaxCaseDefinition(lExp, "dotNotationField",
                (n, s) -> new DotNotationField(s.convert(n.get(0)), s.convert(n.get(2))),
                rExp, dotToken, ident);
        SyntaxCaseDefinition asteriskOperation = new SyntaxCaseDefinition(rExp, "asteriskOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        new BinaryOperationMethodCall(a, "asterisk", b)),
                rExp, asteriskToken, rExp);
        SyntaxCaseDefinition slashOperation = new SyntaxCaseDefinition(rExp, "slashOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        new BinaryOperationMethodCall(a, "slash", b)),
                rExp, slashToken, rExp);
        SyntaxCaseDefinition percentSignOperation = new SyntaxCaseDefinition(rExp, "percentSignOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        new BinaryOperationMethodCall(a, "percentSign", b)),
                rExp, percentSignToken, rExp);
        SyntaxCaseDefinition minusOperation = new SyntaxCaseDefinition(rExp, "minusOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        new BinaryOperationMethodCall(a, "minus", b)),
                rExp, minusToken, rExp);
        SyntaxCaseDefinition plusOperation = new SyntaxCaseDefinition(rExp, "plusOperation",
                new CBOConverterMethod<RValue>((a, b) ->
                        new BinaryOperationMethodCall(a, "plus", b)),
                rExp, plusToken, rExp);
        SyntaxCaseDefinition arrayLiteral = new SyntaxCaseDefinition(rExp, "arrayLiteral",
                (n, s) -> (RValue) m -> {
                    CommaSeparatedExpressionList cselist = s.convert(n.get(1));
                    return cselist.generateToolList(m);
                },
                openSquareBracketToken, csel, closedSquareBracketToken);
        SyntaxCaseDefinition ifThenElseStatement = new SyntaxCaseDefinition(rExp, "ifThenElseStatement",
                (n, s) -> new IfThenElseStatement(s.convert(n.get(1)), s.convert(n.get(3)), s.convert(n.get(5))),
                ifToken, rExp, thenToken, rExp, elseToken, rExp).parsingDirection(SyntaxCase.ParsingDirection.RightToLeft);
        SyntaxCaseDefinition ifThenStatement = new SyntaxCaseDefinition(rExp, "ifThenStatement",
                (n, s) -> new IfThenStatement(s.convert(n.get(1)), s.convert(n.get(3))),
                ifToken, rExp, thenToken, rExp).parsingDirection(SyntaxCase.ParsingDirection.RightToLeft);
        SyntaxCaseDefinition whileStatement = new SyntaxCaseDefinition(rExp, "whileStatement",
                (n, s) -> new WhileStatement(s.convert(n.get(1)), s.convert(n.get(3))),
                whileToken, rExp, doToken, rExp);
        SyntaxCaseDefinition assignment = new SyntaxCaseDefinition(rExp, "assignment",
                (n, s) -> new Assignment(s.convert(n.get(0)), s.convert(n.get(2))),
                lExp, assignmentOperatorToken, rExp).parsingDirection(SyntaxCase.ParsingDirection.RightToLeft);
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
                newVarDeclaration,
                dotNotationField,
                asteriskOperation, slashOperation, percentSignOperation,
                minusOperation, plusOperation,
                arrayLiteral,
                ifThenElseStatement, ifThenStatement,
                whileStatement,
                assignment,
                commaSeparatedExpressionListBase,
                commaSeparatedExpressionListStep,
                sequentialComposition
        };
        return new ProgramGenerator(lexicon, grammar);
    }

    public static void main(String[] args) {
        test2();
    }
}
