package com.parsleyj.tool.semantics.parameter;

import com.parsleyj.tool.semantics.base.ParameterDefinition;
import com.parsleyj.tool.semantics.base.ParameterDefinitionList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ParameterDefinitionListImpl implements ParameterDefinitionList {
    public List<ParameterDefinition> parameterDefinitions = new ArrayList<>();
    public ParameterDefinitionListImpl(ParameterDefinition parameterDefinition, ParameterDefinition parameterDefinition1) {
        this.parameterDefinitions.add(parameterDefinition);
        this.parameterDefinitions.add(parameterDefinition1);
    }

    public ParameterDefinitionListImpl(ParameterDefinitionList list, ParameterDefinition parameterDefinition){
        this.parameterDefinitions.addAll(list.getParameterDefinitions());
        this.parameterDefinitions.add(parameterDefinition);
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
}
