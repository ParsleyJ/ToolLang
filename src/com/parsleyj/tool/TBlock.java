package com.parsleyj.tool;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 23/12/15.
 */
public class TBlock extends TStatement {

    private ArrayList<TStatement> statements = new ArrayList<TStatement>();

    public ArrayList<TStatement> getStatements() {
        return statements;
    }

    public void setStatements(ArrayList<TStatement> statements) {
        this.statements = statements;
    }

    @Override
    public TObject evaluate(TNamespace namespace, ToolInterpreter interpreter) {
        TObject res = TBaseTypes.NULL_OBJECT;
        namespace.pushNewStack();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < statements.size(); i++) {
            TStatement statement = statements.get(i);
            res = statement.evaluate(namespace, interpreter);
            if(res.getTClass().isOrExtends(TBaseTypes.THROWED_ERROR_CLASS)) break;
        }
        namespace.popStack();
        return res;
    }

    //// TODO: array of statements... evaluate = each statement in order...

}
