package com.parsleyj.tool.interpreter;

import com.parsleyj.tool.TNamespace;

import java.util.List;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class ToolScanner {

    private List<TokenPattern> tokenPatterns;

    public ToolScanner(List<TokenPattern> tokenPatterns) {
        this.tokenPatterns = tokenPatterns;
    }

    public List<Token> getTokens(String string, TNamespace memory, List<TokenPattern> literalDefinitionList) {
        //TODO: search for patterns in string and generate Tokens
        return null;
    }
}
