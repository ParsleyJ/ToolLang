package com.parsleyj.tool;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Giuseppe on 19/05/15.
 */
public class TBaseTypes {

    public static final TObject NULL_OBJECT = new TObject(null, null) {
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public boolean isNullValue() {
            return true;
        }
    };

    public static final TClass OBJECT_CLASS = new TClass("Object", null);

    public static final TObject TOOL_OBJECT = new TObject(OBJECT_CLASS, null);

    public static final TClass METAOBJECT_CLASS = new TClass("___MetaObject___", OBJECT_CLASS);//TODO: define in java
    public static final TClass CLASS_CLASS = new TClass("___Class___", METAOBJECT_CLASS) {
        /**
         * Call this to define a new class
         * @param classCreationParameters must be:
         *                                0 => the TIdentifier name of the class
         *                                1 => the parent TClass
         *                                2... => the TMethods and TFields in the class body
         * @return a new TClass with the defined name, parent class, methods and fields.
         */
        @Override
        public TObject newInstance(TObject... classCreationParameters) {

            TIdentifier className = new TIdentifier("");
            TClass parentClass = OBJECT_CLASS;
            ArrayList<TMethod> methods = new ArrayList<TMethod>();
            ArrayList<TField> fields = new ArrayList<TField>();

            //TODO: !!! allow creation of anonymous classes
            if(classCreationParameters.length == 0){
                return InternalUtils.throwError(INVALID_CALL_ERROR_CLASS,
                        "Failed to create a class with no name, no parent class, no body.");
            }else{
                for (int i = 0; i < classCreationParameters.length; i++){
                    if(i==0){
                        if(classCreationParameters[i].getTClass().isOrExtends(IDENTIFIER_CLASS)){
                            className = (TIdentifier) classCreationParameters[i];
                        } else {
                            return InternalUtils.throwError(INVALID_CLASS_DEFINITION_ERROR_CLASS,
                                    "Failed to create a class with invalid name.");
                        }
                    }else if(i==1){
                        if(classCreationParameters[i].getTClass().isOrExtends(CLASS_CLASS)){
                            parentClass = (TClass) classCreationParameters[i];
                        } else {
                            return InternalUtils.throwError(INVALID_CLASS_DEFINITION_ERROR_CLASS,
                                    "Failed to create a class with invalid parent class.");
                        }
                    } else {
                        if(classCreationParameters[i].getTClass().isOrExtends(METHOD_CLASS)){
                            methods.add((TMethod) classCreationParameters[i]);
                        } else if(classCreationParameters[i].getTClass().isOrExtends(FIELD_CLASS)) {
                            fields.add((TField) classCreationParameters[i]);
                        } else {
                            return InternalUtils.throwError(INVALID_CLASS_DEFINITION_ERROR_CLASS,
                                    "Failed to create a class with an invalid method or field.");
                        }
                    }
                }
            }

            TClass result = new TClass(className.getIdentifierString(), parentClass);
            for(TMethod method: methods){
                result.addClassMethod(method);
            }
            for(TField field: fields){
                result.addClassField(field);
            }
            return result;
        }
    };
    public static final TClass KEYWORD_CLASS = new TClass("___Keyword___", METAOBJECT_CLASS);
    public static final TClass STATEMENT_CLASS = new TClass("___Statement___", METAOBJECT_CLASS);
    public static final TClass BLOCK_CLASS = new TClass("___Block___", STATEMENT_CLASS);
    public static final TClass METHOD_CLASS = new TClass("___Method___", BLOCK_CLASS);
    public static final TClass NATIVE_METHOD_CLASS = new TClass("___NativeMethod___", METHOD_CLASS);
    public static final TClass IDENTIFIER_CLASS = new TClass("___Identifier___", METAOBJECT_CLASS);
    public static final TClass METHOD_CALL_CLASS = new TClass("___MethodCall___", METAOBJECT_CLASS);
    public static final TClass REFERENCE_CLASS = new TClass("___Reference___", METAOBJECT_CLASS);
    public static final TClass FIELD_CLASS = new TClass("___Field___", METAOBJECT_CLASS);
    public static final TClass FORMAL_PARAMETER_CLASS = new TClass("___FormalParameter___", METAOBJECT_CLASS);
    public static final TClass OPERATOR_CLASS = new TClass("___Operator___", METAOBJECT_CLASS);
    public static final TClass THROWED_ERROR_CLASS = new TClass("___ThrowedError___", METAOBJECT_CLASS);

    public static final TClass TOOL_INTERPRETER_CLASS = new TClass("ToolInterpreter", OBJECT_CLASS);
    public static final DefaultToolInterpreter DEFAULT_INTERPRETER_OBJECT = new DefaultToolInterpreter();
    //TODO: NATIVE_CLASS_CLASS, NATIVE_OBJECT_CLASS

    public static final TClass STRING_CLASS = new TClass("String", OBJECT_CLASS);
    public static final TClass INTEGER_CLASS = new TClass("Integer", OBJECT_CLASS);
    public static final TClass FLOAT_CLASS = new TClass("Float", OBJECT_CLASS);
    public static final TClass BOOLEAN_CLASS = new TClass("Boolean", OBJECT_CLASS);
    public static final TClass COLLECTION_CLASS = new TClass("Collection", OBJECT_CLASS);
    public static final TClass LIST_CLASS = new TClass("List", COLLECTION_CLASS);
    public static final TClass PAIR_CLASS = new TClass("Pair", COLLECTION_CLASS);
    public static final TClass MAP_CLASS = new TClass("Map", COLLECTION_CLASS);

    public static final TClass ERROR_CLASS = new TClass("Error", OBJECT_CLASS);
    public static final TClass INVALID_CALL_ERROR_CLASS = new TClass("InvalidCallError", ERROR_CLASS);
    public static final TClass METHOD_NOT_FOUND_ERROR_CLASS = new TClass("MethodNotFoundError", ERROR_CLASS);
    public static final TClass REFERENCE_NOT_FOUND_ERROR_CLASS = new TClass("ReferenceNotFoundError", ERROR_CLASS);
    public static final TClass INVALID_CLASS_DEFINITION_ERROR_CLASS = new TClass("InvalidClassDefinitionError", ERROR_CLASS);
    public static final TClass INDEX_OUT_OF_BOUNDS_ERROR_CLASS = new TClass("IndexOutOfBoundsError", ERROR_CLASS);



    public static final TObject[] baseObjects = new TObject[]{
            NULL_OBJECT,
            TOOL_OBJECT,
            DEFAULT_INTERPRETER_OBJECT,
    };

    public static final TClass[] baseClasses = new TClass[]{
            OBJECT_CLASS,

            METAOBJECT_CLASS,
            CLASS_CLASS,
            KEYWORD_CLASS,
            STATEMENT_CLASS,
            BLOCK_CLASS,
            METHOD_CLASS,
            NATIVE_METHOD_CLASS,
            IDENTIFIER_CLASS,
            METHOD_CALL_CLASS,
            REFERENCE_CLASS,
            FIELD_CLASS,
            FORMAL_PARAMETER_CLASS,
            OPERATOR_CLASS,
            THROWED_ERROR_CLASS,

            TOOL_INTERPRETER_CLASS,

            STRING_CLASS,
            INTEGER_CLASS,
            FLOAT_CLASS,
            BOOLEAN_CLASS,
            COLLECTION_CLASS,
            LIST_CLASS,
            PAIR_CLASS,
            MAP_CLASS,

            ERROR_CLASS,
            INVALID_CALL_ERROR_CLASS,
            METHOD_NOT_FOUND_ERROR_CLASS,
            REFERENCE_NOT_FOUND_ERROR_CLASS,
            INVALID_CLASS_DEFINITION_ERROR_CLASS,
            INDEX_OUT_OF_BOUNDS_ERROR_CLASS
    };


    static {
        /************** NULL OBJECT **********************/


        //


        /************** OBJECT CLASS *********************/
        OBJECT_CLASS.addClassMethod(new TNativeMethod("___accessMember___", Collections.singletonList(IDENTIFIER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... values) {
                //access to field, return value
                return self.getNamespace().getReferencedObject(((TIdentifier)values[0]).getIdentifierString());
            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("___accessMember___", Collections.singletonList(METHOD_CALL_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                TMethodCall methodCall = (TMethodCall) paramValues[0];
                //access to method, invoke it and return returned value
                return self.callMethod(methodCall.getName(), methodCall.getActualParameters().toArray(
                        new TObject[methodCall.getActualParameters().size()]));

            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("___getInstanceMethod___", Collections.singletonList(METHOD_CALL_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                TMethodCall methodCall = (TMethodCall) paramValues[0];
                return self.getInstanceMethods().get(TMethod.getCompleteNameFromActual(
                        methodCall.getName().getIdentifierString(), methodCall.getActualParameters().toArray(
                                new TObject[methodCall.getActualParameters().size()])));
            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("getClass", Collections.<TClass>emptyList()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return self.getTClass();
            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("getCompleteInstanceMethodNames", new ArrayList<TClass>()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                ArrayList<TObject> methodNames = new ArrayList<TObject>();
                for(TMethod method: self.getInstanceMethods().values()){
                    methodNames.add(InternalUtils.newStringInstance(method.getCompleteName()));
                }

                return InternalUtils.newListInstance(methodNames);
            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("___delete___", Collections.<TClass>emptyList()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                TObject.BASE_MEMORY.delete(self.getId());
                return NULL_OBJECT;
            }
        });

        OBJECT_CLASS.addClassMethod(new TNativeMethod("___plainInit___", Collections.<TClass>emptyList()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                //for each field of the belonging class, creates a reference (to NULL) in the object's namespace
                for(TField field:self.getTClass().getClassFields()){
                    self.getNamespace().addNullReferenceToScope(field.getFieldName().getIdentifierString());
                }
                //returns the caller object
                return self;
            }
        });

        //TODO: getSimpleInstanceMethodNames --- simple names of instance methods
        //TODO: getSimpleMethodNames --- simple names of all methods callable on this object
        //TODO: getCompleteMethodNames --- complete names of all methods callable on this object
        //TODO: isInstanceOf
        //TODO: equals
        //TODO: castTo --- gets a target class as parameter. casting to the same class or a parent class should be predefined
        //TODO: toString --- or getStringRepresentation
        //TODO: clone?

        /************** TOOL OBJECT **********************/
        TNamespace rootNamespace = TOOL_OBJECT.getNamespace();
        rootNamespace.addObjectToScope("null", NULL_OBJECT);
        rootNamespace.addObjectToScope("___Tool___", TOOL_OBJECT);
        rootNamespace.addObjectToScope("___ToolInterpreter___", DEFAULT_INTERPRETER_OBJECT);

        //adds all the base classes to memory
        for (TClass tClass : baseClasses) {
            rootNamespace.addClassToScope(tClass);
        }
        /************** METAOBJECT CLASS : OBJECT ********/


        /************** CLASS CLASS : METAOBJECT *********/
        CLASS_CLASS.addClassMethod(new TNativeMethod("___alloc___", Collections.<TClass>emptyList()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                //generates a instance object from this class and adds it to the heap
                TObject obj = ((TClass) self).newInstance();
                TObject.BASE_MEMORY.put(obj.getId(), obj);
                return obj;
            }
        });


        CLASS_CLASS.addClassMethod(new TNativeMethod("getParentClass", Collections.<TClass>emptyList()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return ((TClass) self).getParentClass();
            }
        });

        CLASS_CLASS.addClassMethod(new TNativeMethod("isOrExtends", Collections.singletonList(CLASS_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(BOOLEAN_CLASS, ((TClass) self).isOrExtends((TClass)paramValues[0]));
            }
        });

        /************** KEYWORD CLASS : METAOBJECT *******/


        /************** STATEMENT CLASS : METAOBJECT *****/


        /************** BLOCK CLASS : STATEMENT *********/


        /************** METHOD CLASS : METAOBJECT ********/


        /************** NATIVE METHOD CLASS : METAOBJECT */


        /************** IDENTIFIER CLASS : METAOBJECT ****/


        /************** METHOD CALL CLASS : METAOBJECT ***/


        /************** REFERENCE CLASS : METAOBJECT *****/


        /*********** FORMAL PARAMETER CLASS :METAOBJECT **/


        /************** FIELD CLASS : METAOBJECT *********/


        /************** OPERATOR CLASS : METAOBJECT ******/
        //operator characters: !$%^&*_+|~-=:;<>?,./
        OPERATOR_CLASS.addLiteralClass(new TLiteral(OPERATOR_CLASS, "/[-!$%^&*_+|~=:;<>?,.\\/]/") {
            @Override
            public TObject convertLiteralToTObject(String literalInstance) {
                //TODO: impl
                return null;
            }
        });

        /************** TOOL INTERPRETER CLASS : OBJECT **/


        /************** DEFAULT INTERPRETER OBJECT *******/
    

        /************** STRING CLASS : OBJECT ************/
        STRING_CLASS.addLiteralClass(new TLiteral(STRING_CLASS, "([\"'])(?:(?=(\\\\?))\\2.)*?\\1") {
            @Override
            public TObject convertLiteralToTObject(String literalInstance) {
                return InternalUtils.newStringInstance(StringUtils.unescapeJavaString(
                        StringUtils.removeFirstAndLastCharacters(literalInstance)));
            }
        });

        STRING_CLASS.addClassMethod(new TNativeMethod("getCharAt", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                Integer index = (Integer)paramValues[0].getPrimitiveValue();
                String s = (String)self.getPrimitiveValue();
                if(index < 0 || index >= s.length()){
                    return InternalUtils.throwError(INDEX_OUT_OF_BOUNDS_ERROR_CLASS,
                            "Tried to access a string of size " + s.length() + " at index " + index + ".");
                }
                return InternalUtils.newStringInstance(String.valueOf(s.charAt(index)));
            }
        });

        STRING_CLASS.addClassMethod(new TNativeMethod("concat", Collections.singletonList(STRING_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                String s1 = (String) self.getPrimitiveValue();
                String s2 = (String) paramValues[0].getPrimitiveValue();
                return InternalUtils.newStringInstance(s1 + s2);
            }
        });

        /************** INTEGER CLASS : OBJECT ***********/
        INTEGER_CLASS.addLiteralClass(new TLiteral(INTEGER_CLASS, "(?<=\\s|^)[-+]?\\d+(?=\\s|$)") { //TODO: support to hex, bin, oct
            @Override
            public TObject convertLiteralToTObject(String literalInstance) {
                return new TObject(INTEGER_CLASS, Integer.parseInt(literalInstance));
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("parseInt", Collections.singletonList(STRING_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, Integer.parseInt((String) paramValues[0].getPrimitiveValue()));
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("add", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer) self.getPrimitiveValue() + (Integer) paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("subtract", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer) self.getPrimitiveValue() - (Integer) paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("multiply", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer) self.getPrimitiveValue() * (Integer) paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("divide", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer) self.getPrimitiveValue() / (Integer) paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("mod", Collections.singletonList(INTEGER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer) self.getPrimitiveValue() % (Integer) paramValues[0].getPrimitiveValue());
            }
        });

        /************** FLOAT CLASS : OBJECT *************/
        /************** BOOLEAN CLASS : OBJECT ***********/
        /************** COLLECTION CLASS : OBJECT ********/
        /************** LIST CLASS : COLLECTION **********/
        /************** PAIR CLASS : COLLECTION **********/
        /************** MAP CLASS : COLLECTION ***********/




    }

    /*********************************************************/
    /************************ OPERATORS **********************/
    /*********************************************************/

    public static final TOperator DOT_OPERATOR = new TOperator(
            ".",
            new TIdentifier("___accessMember___"),
            TOperator.OperatorBehavior.Binary,
            TOperator.OperatorAssociativity.Left);

    public static final TOperator ADD_OPERATOR = new TOperator(
            "+",
            new TIdentifier("add"),
            TOperator.OperatorBehavior.Binary,
            TOperator.OperatorAssociativity.Left);
}
