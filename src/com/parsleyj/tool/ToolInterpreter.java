package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class ToolInterpreter {

    private ToolScanner scanner;

    private ToolMemory memory;

    public ToolInterpreter(ToolScanner scanner) {
        this.scanner = scanner;
        this.memory = TBaseTypes.TOOL_OBJECT.getNamespace();
        //...
        //memory.addToRootScope(...);
        //memory.addToRootScope(...);
        //memory.addToRootScope(...);
        //...
    }


    public TObject evaluateExpression(String expression) {
        List<TToken> tokens = scanner.getTokens(expression);

        return null;
    }

    public TObject evaluateExpression(List<TToken> tokens) {
        return null;
    }

}
