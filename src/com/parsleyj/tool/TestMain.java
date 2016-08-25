package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.*;
import com.parsleyj.tool.semantics.base.*;
import com.parsleyj.tool.semantics.nametabled.*;
import com.parsleyj.tool.semantics.parameter.ExplicitTypeParameterDefinition;
import com.parsleyj.toolparser.program.*;
import com.parsleyj.utils.Lol;

import java.util.*;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class TestMain {

    public static final boolean PRINT_DEBUG = false;
    public static final boolean PRINT_TOOL_EXCEPTION_STACK_TRACE = false;
    public static final boolean MULTILINE = true;
    public static final boolean PRINT_RESULTS = false;

    public static void test() {
        //Lol.addFlag("memoryStack");
        Program.VERBOSE = PRINT_DEBUG;
        Scanner sc = new Scanner(System.in);
        String memName = "M";
        Memory memory = new Memory(memName);
        ToolObject firstObject = new ToolObject(memory, null);//TODO: change with the correct "global" object
        memory.pushCallFrame(firstObject, new ArrayDeque<>());
        memory.init();
        firstObject.forceSetBelongingClass(memory.baseTypes().C_OBJECT);
        memory.pushScope();
        memory.loadBaseObjects();

        Interpreter interp = ToolGrammar.getDefaultInterpreter(memory);
        interp.setPrintDebugMessages(PRINT_DEBUG);
        interp.initParser(ToolGrammar.rExp);
        while (true) {
            StringBuilder sb = new StringBuilder();
            if (MULTILINE) {
                while (!sb.toString().endsWith("\n\n")) {
                    String l = sc.nextLine()+"\n";
                    sb.append(l);
                }
            } else {
                sb.append(sc.nextLine());
            }
            String programString = sb.toString();

            if (programString.equals("exit")) break;

            try {
                Program prog = interp.interpret("testParsed", programString, (p, c) -> {
                    RValue e = (RValue) p.getRootSemanticObject();
                    try {
                        ToolObject to = e.evaluate((Memory) c.getConfigurationElement(memName));
                        if (PRINT_RESULTS) System.out.println("RESULT = " + to);
                    } catch (ToolNativeException e1) {
                        if (PRINT_TOOL_EXCEPTION_STACK_TRACE) e1.printStackTrace();
                        System.err.println("Tool Exception not handled of type " + e1.getExceptionObject().getBelongingClass().getClassName() + ": " + e1.getExceptionObject().getExplain());
                        System.err.println(e1.getFrameTrace());
                    }
                    return true;
                });
                prog.executeProgram(memory);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        test();
    }
}
