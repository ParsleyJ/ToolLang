package com.parsleyj.tool.semantics.base;

import com.parsleyj.toolparser.semanticsconverter.SemanticObject;

import java.util.List;

/**
 * Created by Giuseppe on 31/05/16.
 * TODO: javadoc
 */
public interface ParameterDefinitionList extends SemanticObject {
    List<ParameterDefinition> getParameterDefinitions();
}
