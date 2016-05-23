package com.parsleyj.tool.semantics.base;

import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ParameterDefinitionList implements SemanticObject{
    public List<ParameterDefinition> parameterDefinitions = new ArrayList<>();
    public ParameterDefinitionList(ParameterDefinition parameterDefinition, ParameterDefinition parameterDefinition1) {
        this.parameterDefinitions.add(parameterDefinition);
        this.parameterDefinitions.add(parameterDefinition1);
    }

    public ParameterDefinitionList(ParameterDefinitionList list, ParameterDefinition parameterDefinition){
        this.parameterDefinitions.addAll(list.getParameterDefinitions());
        this.parameterDefinitions.add(parameterDefinition);
    }

    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }
}
