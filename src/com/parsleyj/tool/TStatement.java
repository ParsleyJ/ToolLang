package com.parsleyj.tool;

import com.parsleyj.tool.interpreter.ToolInterpreter;

/**
 * Created by Giuseppe on 23/12/15.
 */
public class TStatement extends TObject {

    public TStatement() {
        super(TBaseTypes.STATEMENT_CLASS);
    }

    //for TBlock
    protected TStatement(TClass belongingClass){
        super(belongingClass);
    }

    public TObject evaluate(TNamespace namespace, ToolInterpreter interpreter){
        //TODO: impl
        return null;
    }
}
