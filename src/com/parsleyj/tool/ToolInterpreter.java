package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class ToolInterpreter extends TObject{



    private ToolScanner scanner;

    private TNamespace memory;

    public ToolInterpreter(ToolScanner scanner) {
        super(TBaseTypes.TOOL_INTERPRETER_CLASS);
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
