package com.parsleyj.tool;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 23/12/15.
 */
public class TBlock extends TStatement {

    private ArrayList<TStatement> bodyStatements = new ArrayList<TStatement>();

    public TBlock(ArrayList<TStatement> bodyStatements){
        super(TBaseTypes.BLOCK_CLASS);
        this.bodyStatements = bodyStatements;
    }

    protected TBlock(TClass belongingClass, ArrayList<TStatement> bodyStatements){
        super(belongingClass);
        this.bodyStatements = bodyStatements;
    }

    public ArrayList<TStatement> getBodyStatements() {
        return bodyStatements;
    }

    public void setBodyStatements(ArrayList<TStatement> bodyStatements) {
        this.bodyStatements = bodyStatements;
    }

    @Override
    public TObject evaluate(TNamespace namespace, ToolInterpreter interpreter) {
        TObject res = TBaseTypes.NULL_OBJECT;
        namespace.pushNewStack();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < bodyStatements.size(); i++) {
            TStatement statement = bodyStatements.get(i);
            res = statement.evaluate(namespace, interpreter);
            if(res.getTClass().isOrExtends(TBaseTypes.THROWED_ERROR_CLASS)) break;
        }
        namespace.popStack();
        return res;
    }

}
