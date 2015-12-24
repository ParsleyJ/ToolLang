package com.parsleyj.tool.interpreter;

/**
 * Created by Giuseppe on 18/05/15.
 */
public class TokenPattern {
    private String regex;
    private String tokenClassName;

    public TokenPattern(String regex, String tokenClassName) {
        this.regex = regex;
        this.tokenClassName = tokenClassName;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getTokenClassName() {
        return tokenClassName;
    }

    public void setTokenClassName(String tokenClassName) {
        this.tokenClassName = tokenClassName;
    }
}
