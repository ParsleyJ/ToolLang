package com.parsleyj.tool.semantics.base;

import java.util.List;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public class IdentifierList extends LValueList implements ParameterDefinitionList {

    public IdentifierList(Identifier a, Identifier b) {
        super(a, b);
    }

    public IdentifierList(IdentifierList a, Identifier b) {
        super(a, b);
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return null;
    }


}
