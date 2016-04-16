package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.BadMethodCallException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BaseTypes {

    public static final ToolObject O_NULL = new ToolObject(null){//TODO: special Null class: is or extends returns always true
        @Override
        public boolean isNull() {
            return true;
        }
    }; //todo: null class? not a null object but an "external value"?
    public static final ToolClass C_OBJECT = new ToolClass("Object", null);
    public static final ToolClass C_TOOL = new ToolClass("Tool", C_OBJECT);
    public static final ToolClass C_CLASS = new ToolClass("Class", C_OBJECT);
    public static final ToolClass C_REFERENCE = new ToolClass("Reference", C_OBJECT);
    public static final ToolClass C_FIELD = new ToolClass("Field", C_OBJECT);
    public static final ToolClass C_BLOCK = new ToolClass("Block", C_OBJECT);
    public static final ToolClass C_METHOD = new ToolClass("Method", C_OBJECT);
    public static final ToolClass C_METHOD_CALL = new ToolClass("MethodCall", C_OBJECT);
    public static final ToolClass C_INTEGER = new ToolClass("Integer", C_OBJECT);
    public static final ToolClass C_STRING = new ToolClass("String", C_OBJECT);
    public static final ToolClass C_BOOLEAN = new ToolClass("Boolean", C_OBJECT);
    public static final ToolClass C_LIST = new ToolClass("List", C_OBJECT);
    public static final ToolClass C_EXCEPTION = new ToolClass("Exception", C_OBJECT);
    public static final ToolExceptionClass C_REFERENCE_ALREADY_EXISTS_EXCEPTION = new ToolExceptionClass("ReferenceAlreadyExistsException");
    public static final ToolExceptionClass C_REFERENCE_NOT_FOUND_EXCEPTION = new ToolExceptionClass("ReferenceNotFoundException");
    public static final ToolExceptionClass C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION = new ToolExceptionClass("InvalidConditionalExpressionException");
    public static final ToolExceptionClass C_BAD_METHOD_CALL_EXCEPTION = new ToolExceptionClass("BadMethodCallException");
    public static final ToolExceptionClass C_AMBIGUOUS_METHOD_CALL_EXCEPTION = new ToolExceptionClass("AmbiguousMethodCallException");
    public static final ToolExceptionClass C_METHOD_NOT_FOUND_EXCEPTION = new ToolExceptionClass("MethodNotFoundException");
    public static final ToolExceptionClass C_CALL_ON_NULL_EXCEPTION = new ToolExceptionClass("CallOnNullException");
    public static final ToolExceptionClass C_ARITHMETIC_EXCEPTION = new ToolExceptionClass("ArithmeticException");

    static{
        C_TOOL.addClassMethod(new ToolMethod(
                Visibility.Public,
                "print",
                new ParameterDefinition[]{
                        new ParameterDefinition("x", C_INTEGER)
                }, memory -> {
                    ToolObject x = memory.getObjectByIdentifier("x");
                    if(x.getBelongingClass().isOrExtends(C_INTEGER)){
                        System.out.println("PRINT: "+x.getPrintString());
                        return x;
                    } else throw new BadMethodCallException("Something went wrong while attempting to call <Integer>.print(Integer)");
                }
        ));

        C_INTEGER.addInstanceMethods(ToolInteger.getNativeInstanceMethods());
        C_INTEGER.addClassMethods(ToolInteger.getNativeClassMethods());

        C_CLASS.getInstanceMethodTable().add(new ToolMethod(
                Visibility.Public,
                "_create_", new ParameterDefinition[]{}, memory -> {
            ToolObject self = memory.getSelfObject();
            ToolObject result = null;
            if(self.getBelongingClass().isOrExtends(C_CLASS)){
                ToolClass selfClass = (ToolClass) self;
                result = new ToolObject(selfClass);
                for(Field field:selfClass.getFields().values()){
                    //all set to null
                    result.addReferenceMember(new Reference(field.getIdentifier()));
                }
            } else throw new BadMethodCallException("Something went wrong while attempting to call <Class>._create_()");
            return result;
        }));


        C_BLOCK.getInstanceMethodTable().add(new ToolMethod(
                Visibility.Public,
                "execute", new ParameterDefinition[]{}, memory -> {
            ToolObject self = memory.getObjectByIdentifier("this");
            ToolObject result = null;
            if(self.getBelongingClass().equals(C_BLOCK)){
                result = ((ToolBlock) self).execute(memory);
            } else throw new BadMethodCallException("Something went wrong while attempting to call <Block>.execute()");//TODO: best constructor for this exception
            return result;
        }));
    }

    public static List<ToolClass> getAllBaseClasses(){
        return Arrays.asList(
                C_OBJECT,
                C_TOOL,
                C_CLASS,
                C_REFERENCE,
                C_FIELD,
                C_BLOCK,
                C_METHOD,
                C_METHOD_CALL,
                C_INTEGER,
                C_STRING,
                C_BOOLEAN,
                C_LIST,
                C_EXCEPTION,
                C_REFERENCE_ALREADY_EXISTS_EXCEPTION,
                C_REFERENCE_NOT_FOUND_EXCEPTION,
                C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION,
                C_BAD_METHOD_CALL_EXCEPTION,
                C_METHOD_NOT_FOUND_EXCEPTION
        );
    }

    //todo visibility of fields
    //todo visibility of methods
    //todo reference types?
    //todo better object id type, not Integer please
    //todo generic this/self pointer
    //todo liskov
    //todo list types
    //todo in method definitions, use a "isOfTypeOrExtendingType(ToolClass)" method

}
