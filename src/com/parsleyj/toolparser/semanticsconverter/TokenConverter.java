package com.parsleyj.toolparser.semanticsconverter;

import com.parsleyj.toolparser.tokenizer.TokenCategory;

/**
 * Contains the method and the metadata to convert a node
 * representing a token category in a {@link SemanticObject}.
 */
public class TokenConverter {
    private TokenCategory tokenCategory;
    private TokenConverterMethod method;

    public TokenConverter(TokenCategory tokenCategory, TokenConverterMethod method) {
        this.tokenCategory = tokenCategory;
        this.method = method;
    }

    public TokenCategory getTokenCategory() {
        return tokenCategory;
    }

    public SemanticObject convert(String generatingString, SemanticsConverter s){
        return method.convert(generatingString);
    }

}
