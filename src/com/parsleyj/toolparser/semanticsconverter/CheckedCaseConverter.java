package com.parsleyj.toolparser.semanticsconverter;

import com.parsleyj.toolparser.parser.ParseTreeNode;
import com.parsleyj.toolparser.parser.SyntaxCase;

/**
 * A functional interface used to defineParameter a method to convert a
 * non-terminal {@link ParseTreeNode} to a {@link SemanticObject},
 * after checking if the size of the node's children list is the same of
 * the corresponding case.
 */
public class CheckedCaseConverter extends CaseConverter{
    public CheckedCaseConverter(SyntaxCase casE, CaseConverterMethod method) {
        super(casE, ((node, s) -> {
            if(node.getChildren().size() == casE.getStructure().size()){
                return method.convert(node,s);
            } else throw new InvalidParseTreeException();
        }));
    }
}
