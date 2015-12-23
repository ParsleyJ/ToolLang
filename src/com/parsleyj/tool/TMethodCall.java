package com.parsleyj.tool;

import java.util.List;

/**
 * Created by Giuseppe on 23/12/15.
 */
public class TMethodCall extends TObject {

    private TObject self;
    private TIdentifier name;
    private List<TObject> actualParameters;

    public TMethodCall(TObject self, TIdentifier name, List<TObject> actualParameters) {
        super(TBaseTypes.METHOD_CALL_CLASS);
        this.self = self;
        this.name = name;
        this.actualParameters = actualParameters;
    }

    public TObject getSelf() {
        return self;
    }

    public void setSelf(TObject self) {
        this.self = self;
    }

    public TIdentifier getName() {
        return name;
    }

    public void setName(TIdentifier name) {
        this.name = name;
    }

    public List<TObject> getActualParameters() {
        return actualParameters;
    }

    public void setActualParameters(List<TObject> actualParameters) {
        this.actualParameters = actualParameters;
    }
}
