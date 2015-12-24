package com.parsleyj.tool.interpreter;

import com.parsleyj.tool.TBaseTypes;
import com.parsleyj.tool.TNamespace;
import com.parsleyj.tool.TObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class ToolInterpreter extends TObject {



    private ToolScanner scanner;

    private TNamespace memory;

    private List<LiteralPattern> literalDefinitionList = new ArrayList<>();

    public ToolInterpreter() {
        super(TBaseTypes.TOOL_INTERPRETER_CLASS);
        this.scanner = new ToolScanner(null);
        this.memory = TBaseTypes.TOOL_OBJECT.getNamespace();
    }

    public void addLiteralToList(LiteralPattern l){
        literalDefinitionList.add(l);
    }
    public List<LiteralPattern> getLiteralList(){
        return literalDefinitionList;
    }

    public TObject evaluate(String expression) {
        List<Token> tokens = scanner.getTokens(expression, memory, literalDefinitionList);

        return null;
    }

    public TObject evaluateExpression(List<Token> tokens) {
        return null;
    }

}
