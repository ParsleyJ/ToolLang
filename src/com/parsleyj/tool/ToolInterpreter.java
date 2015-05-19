package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class ToolInterpreter {



    private ToolScanner scanner;



    public TObject evaluateExpression(String expression){
        List<TToken> tokens = scanner.getTokens(expression);
        
        return null;

    }
}
