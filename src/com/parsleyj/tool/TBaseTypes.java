package com.parsleyj.tool;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Giuseppe on 19/05/15.
 */
public class TBaseTypes {
    public static final TClass OBJECT_CLASS = new TClass("Object", null);
    public static final TClass CLASS_CLASS = new TClass("Class", OBJECT_CLASS){
        @Override
        public TObject newInstance(TObject... constructorParameters) {
            //TODO: get name, methods and fields via constructorParameters

            //noinspection UnnecessaryLocalVariable #+@.:
            TClass result = new TClass("", OBJECT_CLASS);

            return result;
        }
    };
    public static final TClass METHOD_CLASS = new TClass("Method", OBJECT_CLASS);
    public static final TClass NATIVE_METHOD_CLASS = new TClass("NativeMethod", METHOD_CLASS);

    public static final TClass STRING_CLASS = new TClass("String", OBJECT_CLASS);
    public static final TClass INTEGER_CLASS = new TClass("Integer", OBJECT_CLASS);
    public static final TClass FLOAT_CLASS = new TClass("Float", OBJECT_CLASS);
    public static final TClass BOOLEAN_CLASS = new TClass("Boolean", OBJECT_CLASS);

    public static final TClass IDENTIFIER_CLASS = new TClass("___Identifier___", null); //TODO: make IHiddenClass extends TClass, or inheritance
    public static final TClass METHOD_CALL_CLASS = new TClass("___MethodCall___", null);
    public static final TClass REFERENCE_CLASS = new TClass("___Reference___", null);

    public static final TClass ERROR_CLASS = new TClass("Error", OBJECT_CLASS);
    public static final TClass INVALID_CALL_ERROR_CLASS = new TClass("InvalidCallError", ERROR_CLASS);
    public static final TClass METHOD_NOT_FOUND_ERROR_CLASS = new TClass("MethodNotFoundError", ERROR_CLASS);

    public static final TObject NULL_OBJECT = new TObject(null, null);

    static {
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



        /************** CLASS CLASS : OBJECT *************/


        /************** STRING CLASS : OBJECT ************/
        STRING_CLASS.addLiteralClass(new TLiteral(STRING_CLASS, "([\"'])(?:(?=(\\\\?))\\2.)*?\\1") {
            @Override
            public TObject convertLiteralToIObject(String literalInstance) {
                return new TObject(
                        STRING_CLASS,
                        StringUtils.unescapeJavaString(
                                StringUtils.removeFirstAndLastCharacters(
                                        literalInstance)
                        )){
                    @Override
                    public String getStringRepresentation() {
                        return (String) getPrimitiveValue(); //TODO: add quotes?
                    }
                };
            }
        });

        /************** INTEGER CLASS : OBJECT ***********/
        INTEGER_CLASS.addLiteralClass(new TLiteral(INTEGER_CLASS, "(?<=\\s|^)[-+]?\\d+(?=\\s|$)") { //TODO: support to hex, bin, oct
            @Override
            public TObject convertLiteralToIObject(String literalInstance) {
                return new TObject(INTEGER_CLASS, Integer.parseInt(literalInstance));
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("parseInt", Collections.singletonList(STRING_CLASS)) {
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, Integer.parseInt((String) paramValues[0].getPrimitiveValue()));
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("add", Collections.singletonList(INTEGER_CLASS)){
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer)self.getPrimitiveValue() + (Integer)paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("subtract", Collections.singletonList(INTEGER_CLASS)){
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer)self.getPrimitiveValue() - (Integer)paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("multiply", Collections.singletonList(INTEGER_CLASS)){
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer)self.getPrimitiveValue() * (Integer)paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("divide", Collections.singletonList(INTEGER_CLASS)){
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer)self.getPrimitiveValue() / (Integer)paramValues[0].getPrimitiveValue());
            }
        });

        INTEGER_CLASS.addInstanceMethod(new TNativeMethod("mod", Collections.singletonList(INTEGER_CLASS)){
            @Override
            public TObject checkedInvoke(TObject self, TObject... paramValues) {
                return new TObject(INTEGER_CLASS, (Integer)self.getPrimitiveValue() % (Integer)paramValues[0].getPrimitiveValue());
            }
        });


    }


    public static TObject newStringInstance(String value){
        return new TObject(STRING_CLASS, value);
    }

}
