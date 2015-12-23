package com.parsleyj.tool;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TOperator extends TObject {

    //Priority is defined by adding the operator to the intrepreter

    public enum OperatorBehavior{
        UnaryLeft,
        UnaryRight,
        Binary
    }

    public enum OperatorAssociativity{
        Left,
        Right
    }

    private String symbol;
    private TIdentifier maskedName;
    private OperatorBehavior behavior;
    private OperatorAssociativity associativity;

    public TOperator(String symbol, TIdentifier maskedName, OperatorBehavior behavior, OperatorAssociativity associativity) {
        super(TBaseTypes.OPERATOR_CLASS);
        this.symbol = symbol;
        this.maskedName = maskedName;
        this.behavior = behavior;
        this.associativity = associativity;
    }

    public String getSymbol() {
        return symbol;
    }

    public TIdentifier getMaskedName() {
        return maskedName;
    }

    public OperatorBehavior getBehavior() {
        return behavior;
    }

    public OperatorAssociativity getAssociativity() {
        return associativity;
    }
}
