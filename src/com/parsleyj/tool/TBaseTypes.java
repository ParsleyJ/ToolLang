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
        @Override
        public TObject newInstance(TObject... constructorParameters) {
            //TODO: get name, methods and fields via constructorParameters

            //noinspection UnnecessaryLocalVariable
            TClass result = new TClass("", OBJECT_CLASS);

            return result;
        }
    };
    public static final TClass KEYWORD_CLASS = new TClass("___Keyword___", METAOBJECT_CLASS);
    public static final TClass STATEMENT_CLASS = new TClass("___Statement___", METAOBJECT_CLASS);
    public static final TClass BLOCK_CLASS = new TClass("___Block___", METAOBJECT_CLASS);
    public static final TClass METHOD_CLASS = new TClass("___Method___", METAOBJECT_CLASS);
    public static final TClass NATIVE_METHOD_CLASS = new TClass("___NativeMethod___", METHOD_CLASS);
    public static final TClass IDENTIFIER_CLASS = new TClass("___Identifier___", METAOBJECT_CLASS);
    public static final TClass METHOD_CALL_CLASS = new TClass("___MethodCall___", METAOBJECT_CLASS);
    public static final TClass REFERENCE_CLASS = new TClass("___Reference___", METAOBJECT_CLASS);
    public static final TClass FIELD_CLASS = new TClass("___Field___", METAOBJECT_CLASS);
    public static final TClass FORMAL_PARAMETER_CLASS = new TClass("___FormalParameter___", METAOBJECT_CLASS);
    public static final TClass OPERATOR_CLASS = new TClass("___Operator___", METAOBJECT_CLASS);

    public static final TClass TOOL_INTERPRETER_CLASS = new TClass("ToolInterpreter", OBJECT_CLASS);
    public static final TObject DEFAULT_INTERPRETER_OBJECT = new TObject(TOOL_INTERPRETER_CLASS, null);
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
            REFERENCE_NOT_FOUND_ERROR_CLASS
    };


    static {
        /************** NULL OBJECT **********************/


        //


        /************** OBJECT CLASS *********************/
        OBJECT_CLASS.addMethod(new TNativeMethod("___access_member___", Collections.singletonList(IDENTIFIER_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... values) {
                //TODO: check type of param, access to field, return
                return null;
            }
        });

        OBJECT_CLASS.addMethod(new TNativeMethod("___access_member___", Collections.singletonList(METHOD_CALL_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                //TODO: check type of param, access to method, call it
                return null;
            }
        });

        OBJECT_CLASS.addMethod(new TNativeMethod("getClass", new ArrayList<TClass>()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return self.getTClass();
            }
        });

        OBJECT_CLASS.addMethod(new TNativeMethod("methodNames", new ArrayList<TClass>()) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                //TODO returns string array
                return null;
            }
        });
        /************** TOOL OBJECT **********************/
        ToolMemory rootMemory = TOOL_OBJECT.getNamespace();
        rootMemory.addObjectToScope("null", NULL_OBJECT);
        rootMemory.addObjectToScope("___Tool___", TOOL_OBJECT);

        //adds all the base classes to memory
        for (TClass tClass : baseClasses) {
            rootMemory.addClassToScope(tClass);
        }
        /************** METAOBJECT CLASS : OBJECT ********/


        /************** CLASS CLASS : METAOBJECT *********/


        /************** KEYWORD CLASS : METAOBJECT *******/


        /************** STATEMENT CLASS : METAOBJECT *****/


        /************** BLOCK CLASS : METAOBJECT *********/


        /************** METHOD CLASS : METAOBJECT ********/


        /************** NATIVE METHOD CLASS : METAOBJECT */


        /************** IDENTIFIER CLASS : METAOBJECT ****/


        /************** METHOD CALL CLASS : METAOBJECT ***/


        /************** REFERENCE CLASS : METAOBJECT *****/


        /*********** FORMAL PARAMETER CLASS :METAOBJECT ***/


        /************** FIELD CLASS : METAOBJECT *********/


        /************** OPERATOR CLASS : METAOBJECT ******/
        //operator chars: !$%^&*_+|~-=:;<>?,./
        OPERATOR_CLASS.addLiteralClass(new TLiteral(OPERATOR_CLASS, "/[-!$%^&*_+|~=:;<>?,.\\/]/") {
            @Override
            public TObject convertLiteralToTObject(String literalInstance) {
                return null;
            }
        });

        /************** TOOL INTERPRETER CLASS : OBJECT **/


        /************** DEFAULT INTERPRETER OBJECT *******/
    

        /************** STRING CLASS : OBJECT ************/
        STRING_CLASS.addLiteralClass(new TLiteral(STRING_CLASS, "([\"'])(?:(?=(\\\\?))\\2.)*?\\1") {
            @Override
            public TObject convertLiteralToTObject(String literalInstance) {
                return new TObject(
                        STRING_CLASS,
                        StringUtils.unescapeJavaString(
                                StringUtils.removeFirstAndLastCharacters(
                                        literalInstance)
                        )) {
                    @Override
                    public String getStringRepresentation() {
                        return "\"" + (String) getPrimitiveValue() + "\""; //TODO: add quotes?
                    }
                };
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


    public static TObject newStringInstance(String value) {
        return new TObject(STRING_CLASS, value);
    }

}
