package com.parsleyj.tool.objects;

import com.parsleyj.tool.*;
import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.utils.Lol;
import com.parsleyj.utils.MapBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BaseTypes {

    public static final ToolObject O_NULL = new ToolObject(null) {//TODO: special Null class: is or extends returns always true
        @Override
        public boolean isNull() {
            return true;
        }
    }; //todo: null class? not a null object but an "external value"?
    public static final ToolClass C_OBJECT = new ToolClass("Object", null);
    public static final ToolClass C_CLASS = new ToolClass("Class", C_OBJECT);
    public static final ToolClass C_TOOL = new ToolClass("Tool", C_OBJECT);//TODO this must be an object
    public static final ToolClass C_FIELD = new ToolClass("ToolField", C_OBJECT);
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

    public static final Map<Class<?>, ToolClass> NATIVE_CLASS_MAP = new MapBuilder<Class<?>, ToolClass>()
            .put(ToolObject.class, C_OBJECT)
            .put(ToolClass.class, C_CLASS)
            .put(ToolField.class, C_FIELD)
            .put(ToolBlock.class, C_BLOCK)
            .put(ToolMethod.class, C_METHOD)
            .put(ToolInteger.class, C_INTEGER)
            .put(ToolString.class, C_STRING)
            .put(ToolBoolean.class, C_BOOLEAN)
            .put(ToolList.class, C_LIST)
            .put(ToolException.class, C_EXCEPTION)
            .get();

    public static List<ToolClass> getAllBaseClasses() {
        return Arrays.asList(
                C_OBJECT,
                C_CLASS,
                C_TOOL,
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
                C_AMBIGUOUS_METHOD_CALL_EXCEPTION,
                C_METHOD_NOT_FOUND_EXCEPTION,
                C_CALL_ON_NULL_EXCEPTION,
                C_ARITHMETIC_EXCEPTION
        );
    }

    static {
        C_OBJECT.forceSetBelongingClass(C_CLASS);
        C_CLASS.forceSetBelongingClass(C_CLASS);

        C_TOOL.addClassMethod(new ToolMethod(
                Visibility.Public,
                "print",
                new ParameterDefinition[]{
                        new ParameterDefinition("x", C_OBJECT)
                }, memory -> {
            ToolObject x = memory.getObjectByIdentifier("x");
            if (x.getBelongingClass().isOrExtends(C_OBJECT)) {
                System.out.println(x.getPrintString());
                return x;
            } else throw new BadMethodCallException("Something went wrong while attempting to call Tool.print(Object)");
        }
        ));


        NATIVE_CLASS_MAP.entrySet().forEach(e -> {
            try {
                loadNativeMethods(e.getKey(), e.getValue());
            } catch (NativeClassLoadFailedException e1) {
                e1.printStackTrace();
            }
        });
    }


    public static void loadNativeMethods(Class<?> nativeClass, ToolClass toolClass) throws NativeClassLoadFailedException {
        for (Method m : nativeClass.getMethods()) {
            if (m.isAnnotationPresent(NativeClassMethod.class) || m.isAnnotationPresent(NativeInstanceMethod.class)) {
                if (!ToolObject.class.isAssignableFrom(m.getReturnType())) //it must return a ToolObject or derivate
                    throw new NativeClassLoadFailedException();


                NativeClassMethod classAnn = m.getDeclaredAnnotation(NativeClassMethod.class);
                NativeInstanceMethod instanceAnn = m.getDeclaredAnnotation(NativeInstanceMethod.class);
                boolean isInstanceMethod = instanceAnn != null;
                Parameter[] nativePars = m.getParameters();
                List<ParameterDefinition> pars = new ArrayList<>();
                boolean hasSelfParameter = false;
                for (int i = 0; i < nativePars.length; i++) {
                    Parameter nativePar = nativePars[i];
                    if (ToolObject.class.isAssignableFrom(nativePar.getType())) {
                        if (i != 0 && nativePar.isAnnotationPresent(SelfParameter.class)) {
                            throw new NativeClassLoadFailedException();
                        } else if (i == 0 && nativePar.isAnnotationPresent(SelfParameter.class)) {
                            hasSelfParameter = true;
                        } else {
                            ToolClass baseType = NATIVE_CLASS_MAP.get(nativePar.getType());
                            if (baseType == null) throw new NativeClassLoadFailedException();
                            pars.add(new ParameterDefinition(nativePar.getName(), baseType));
                        }
                    } else {
                        throw new NativeClassLoadFailedException();
                    }
                }
                if (isInstanceMethod && !hasSelfParameter) throw new NativeClassLoadFailedException();

                try {
                    ToolMethod newMethod = new ToolMethod(
                            isInstanceMethod ? instanceAnn.value() : classAnn.value(),
                            m.getName(), pars.toArray(new ParameterDefinition[pars.size()]),
                            memory -> {
                                List<ToolObject> actualPars = new ArrayList<>();
                                if (isInstanceMethod) {
                                    ToolObject self = memory.getSelfObject();
                                    if (self.getBelongingClass().isOrExtends(toolClass))
                                        actualPars.add(memory.getSelfObject());
                                }
                                for (ParameterDefinition par : pars) {
                                    ToolObject x = memory.getObjectByIdentifier(par.getParameterName());
                                    if (x.getBelongingClass().isOrExtends(par.getParameterType())) {
                                        actualPars.add(x);
                                    } else
                                        throw new BadMethodCallException("Something went wrong while attempting to call a native method"); //TODO specify what method
                                }
                                try {
                                    return (ToolObject) m.invoke(null, actualPars.toArray(new Object[actualPars.size()]));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException("InvokeFailed");
                                }
                            });
                    if(!isInstanceMethod){
                        toolClass.addClassMethod(newMethod);
                    }else{
                        toolClass.addInstanceMethod(newMethod);
                    }
                    Lol.v("Added native method: " + (isInstanceMethod ?
                            newMethod.completeInstanceMethodName(toolClass) :
                            newMethod.completeClassMethodName(toolClass)));

                } catch (RuntimeException e) {
                    if (e.getMessage() != null && e.getMessage().equals("InvokeFailed"))
                        throw new NativeClassLoadFailedException();
                    else
                        throw e;
                }
            }
        }
    }

    public static class NativeClassLoadFailedException extends Exception {
        public NativeClassLoadFailedException() {
        }

        public NativeClassLoadFailedException(String msg) {
            super(msg);
        }
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
