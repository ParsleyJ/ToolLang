package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.*;
import com.parsleyj.tool.objects.exception.ToolException;
import com.parsleyj.tool.objects.exception.ToolExceptionClass;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.ToolMethodPrototype;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.utils.Lol;
import com.parsleyj.utils.MapBuilder;
import com.parsleyj.utils.PJ;
import com.parsleyj.utils.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class BaseTypes {

    public final NativeAlgorithms nativeAlgorithms = new NativeAlgorithms();

    public ToolObject O_NULL;
    // --- BASE CLASSES ---
    public ToolClass C_OBJECT;
    public ToolClass C_CLASS;
    public ToolClass C_INTERFACE;
    public ToolClass C_EXTENSOR;
    public ToolClass C_METHOD_PROTOTYPE;
    public ToolClass C_METHOD_SET;
    public ToolClass C_METHOD;
    public ToolClass C_TUPLE;
    public ToolClass C_INTEGER;
    public ToolClass C_STRING;
    public ToolClass C_BOOLEAN;
    public ToolClass C_LIST;
    public ToolClass C_INTEGER_RANGE;
    public ToolClass C_EXCEPTION;
    public ToolClass C_OPTIONAL;

    // --- EXCEPTIONS ---
    public ToolExceptionClass C_REFERENCE_ALREADY_EXISTS_EXCEPTION;
    public ToolExceptionClass C_REFERENCE_NOT_FOUND_EXCEPTION;
    public ToolExceptionClass C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION;
    public ToolExceptionClass C_BAD_METHOD_CALL_EXCEPTION;
    public ToolExceptionClass C_AMBIGUOUS_METHOD_CALL_EXCEPTION;
    public ToolExceptionClass C_AMBIGUOUS_METHOD_DEFINITION_EXCEPTION;
    public ToolExceptionClass C_METHOD_NOT_FOUND_EXCEPTION;
    public ToolExceptionClass C_CALL_ON_NULL_EXCEPTION;
    public ToolExceptionClass C_NULL_VALUE_EXCEPTION;
    public ToolExceptionClass C_ARITHMETIC_EXCEPTION;
    public ToolExceptionClass C_INVALID_INDEX_TYPE_EXCEPTION;
    public ToolExceptionClass C_INVALID_INDEX_LIST_EXCEPTION;
    public ToolExceptionClass C_INVALID_ITERABLE_EXPRESSION_EXCEPTION;
    public ToolExceptionClass C_INVALID_TYPE_EXPRESSION_EXCEPTION;
    public ToolExceptionClass C_NAME_ALREADY_USED_EXCEPTION;
    public ToolExceptionClass C_VISIBILITY_EXCEPTION;
    public ToolExceptionClass C_INDEX_OUT_OF_BOUNDS_EXCEPTION;
    public ToolExceptionClass C_NAME_NOT_FOUND_EXCEPTION;
    public ToolExceptionClass C_INVALID_DEFINITION_EXCEPTION;
    public ToolExceptionClass C_INVALID_BREAK_EXPRESSION_EXCEPTION;
    public ToolExceptionClass C_INVALID_L_VALUE_EXCEPTION;
    public ToolExceptionClass C_BAD_RETURNED_VALUE_EXCEPTION;

    // --- INTERFACES ---
    public ToolInterface I_TYPE;
    public ToolInterface I_ITERABLE;
    public ToolInterface I_ITERATOR;

    public Map<Class<?>, ToolClass> NATIVE_CLASS_MAP;
    public Map<Class<?>, ToolInterface> NATIVE_INTERFACE_MAP;

    public List<ToolClass> getAllBaseClasses() {
        return Arrays.asList(
                C_OBJECT,
                C_CLASS,
                C_INTERFACE,
                C_EXTENSOR,
                C_METHOD_PROTOTYPE,
                C_METHOD_SET,
                C_METHOD,
                C_TUPLE,
                C_INTEGER,
                C_STRING,
                C_BOOLEAN,
                C_LIST,
                C_INTEGER_RANGE,
                C_EXCEPTION,
                C_OPTIONAL,

                C_REFERENCE_ALREADY_EXISTS_EXCEPTION,
                C_REFERENCE_NOT_FOUND_EXCEPTION,
                C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION,
                C_BAD_METHOD_CALL_EXCEPTION,
                C_AMBIGUOUS_METHOD_CALL_EXCEPTION,
                C_METHOD_NOT_FOUND_EXCEPTION,
                C_CALL_ON_NULL_EXCEPTION,
                C_NULL_VALUE_EXCEPTION,
                C_ARITHMETIC_EXCEPTION,
                C_INVALID_INDEX_TYPE_EXCEPTION,
                C_INVALID_INDEX_LIST_EXCEPTION,
                C_INDEX_OUT_OF_BOUNDS_EXCEPTION,
                C_INVALID_ITERABLE_EXPRESSION_EXCEPTION,
                C_INVALID_TYPE_EXPRESSION_EXCEPTION,
                C_NAME_ALREADY_USED_EXCEPTION,
                C_VISIBILITY_EXCEPTION,
                C_NAME_NOT_FOUND_EXCEPTION,
                C_INVALID_DEFINITION_EXCEPTION,
                C_INVALID_BREAK_EXPRESSION_EXCEPTION,
                C_INVALID_L_VALUE_EXCEPTION,
                C_BAD_RETURNED_VALUE_EXCEPTION
        );
    }

    public List<ToolInterface> getAllBaseInterfaces() {
        return Arrays.asList(
                I_TYPE,
                I_ITERABLE,
                I_ITERATOR
        );
    }


    public void init(Memory m) {
        try {
            O_NULL = new ToolNull(m);

            C_OBJECT = new ToolClass(m, "Object", null);
            C_CLASS = new ToolClass(m, "Class", C_OBJECT);
            C_INTERFACE = new ToolClass(m, "Interface", C_OBJECT);
            C_EXTENSOR = new ToolClass(m, "Extensor", C_OBJECT);
            C_METHOD_PROTOTYPE = new ToolClass(m, "MethodPrototype", C_OBJECT);
            C_METHOD_SET = new ToolClass(m, "MethodSet", C_METHOD_PROTOTYPE);
            C_METHOD = new ToolClass(m, "Method", C_METHOD_PROTOTYPE);
            C_TUPLE = new ToolClass(m, "Tuple", C_OBJECT);
            C_INTEGER = new ToolClass(m, "Integer", C_OBJECT);
            C_STRING = new ToolClass(m, "String", C_OBJECT);
            C_BOOLEAN = new ToolClass(m, "Boolean", C_OBJECT);
            C_LIST = new ToolClass(m, "List", C_OBJECT);
            C_INTEGER_RANGE = new ToolClass(m, "IntegerRange", C_OBJECT);
            C_EXCEPTION = new ToolClass(m, "Exception", C_OBJECT);
            C_OPTIONAL = new ToolClass(m, "Optional", C_OBJECT);

            C_REFERENCE_ALREADY_EXISTS_EXCEPTION = new ToolExceptionClass(m, "ReferenceAlreadyExistsException");
            C_REFERENCE_NOT_FOUND_EXCEPTION = new ToolExceptionClass(m, "ReferenceNotFoundException");
            C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION = new ToolExceptionClass(m, "InvalidConditionalExpressionException");
            C_BAD_METHOD_CALL_EXCEPTION = new ToolExceptionClass(m, "BadMethodCallException");
            C_AMBIGUOUS_METHOD_CALL_EXCEPTION = new ToolExceptionClass(m, "AmbiguousMethodCallException");
            C_AMBIGUOUS_METHOD_DEFINITION_EXCEPTION = new ToolExceptionClass(m, "AmbiguousMethodDefinitionException");
            C_METHOD_NOT_FOUND_EXCEPTION = new ToolExceptionClass(m, "MethodNotFoundException");
            C_CALL_ON_NULL_EXCEPTION = new ToolExceptionClass(m, "CallOnNullException");
            C_NULL_VALUE_EXCEPTION = new ToolExceptionClass(m, "NullValueException");
            C_ARITHMETIC_EXCEPTION = new ToolExceptionClass(m, "ArithmeticException");
            C_INVALID_INDEX_TYPE_EXCEPTION = new ToolExceptionClass(m, "InvalidIndexTypeException");
            C_INVALID_INDEX_LIST_EXCEPTION = new ToolExceptionClass(m, "InvalidIndexListException");
            C_INVALID_ITERABLE_EXPRESSION_EXCEPTION = new ToolExceptionClass(m, "InvalidIterableExpressionException");
            C_INVALID_TYPE_EXPRESSION_EXCEPTION = new ToolExceptionClass(m, "InvalidTypeException");
            C_NAME_ALREADY_USED_EXCEPTION = new ToolExceptionClass(m, "NameAlreadyUsedException");
            C_VISIBILITY_EXCEPTION = new ToolExceptionClass(m, "VisibilityException");
            C_INDEX_OUT_OF_BOUNDS_EXCEPTION = new ToolExceptionClass(m, "IndexOutOfBoundsException");
            C_NAME_NOT_FOUND_EXCEPTION = new ToolExceptionClass(m, "NameNotFoundException");
            C_INVALID_DEFINITION_EXCEPTION = new ToolExceptionClass(m, "InvalidDefinitionException");
            C_INVALID_BREAK_EXPRESSION_EXCEPTION = new ToolExceptionClass(m, "InvalidBreakExpressionException");
            C_INVALID_L_VALUE_EXCEPTION = new ToolExceptionClass(m, "InvalidLValueException");
            C_BAD_RETURNED_VALUE_EXCEPTION = new ToolExceptionClass(m, "BadReturnedValueException");

            I_TYPE = new ToolInterface(m, "Type", Collections.emptyList())
                    .addMethodDeclaration(m,
                            ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
                            "is",
                            new FormalParameter[]{new FormalParameter("arg", C_OBJECT)});
            I_TYPE.addMethodDeclaration(m,
                    ToolMethod.METHOD_CATEGORY_METHOD,
                    "getConvertibility",
                    new FormalParameter[]{new FormalParameter("from", I_TYPE)});
            I_TYPE.addMethodDeclaration(m,
                    ToolMethod.METHOD_CATEGORY_METHOD,
                    "getObjectConvertibility",
                    new FormalParameter[]{new FormalParameter("from", C_OBJECT)});
            I_TYPE.addMethodDeclaration(m,
                    ToolMethod.METHOD_CATEGORY_METHOD,
                    "canBeUsedAs",
                    new FormalParameter[]{new FormalParameter("other", I_TYPE)});
            I_TYPE.addMethodDeclaration(m,
                    ToolGetterMethod.METHOD_CATEGORY_GETTER,
                    "name",
                    new FormalParameter[]{});

            I_ITERABLE = new ToolInterface(m, "Iterable", Collections.emptyList())
                    .addMethodDeclaration(m,
                            ToolGetterMethod.METHOD_CATEGORY_GETTER,
                            "iterator",
                            new FormalParameter[]{});

            I_ITERATOR = new ToolInterface(m, "Iterator", Collections.emptyList())
                    .addMethodDeclaration(m,
                            ToolGetterMethod.METHOD_CATEGORY_GETTER,
                            "hasNext",
                            new FormalParameter[]{})
                    .addMethodDeclaration(m,
                            ToolGetterMethod.METHOD_CATEGORY_GETTER,
                            "next",
                            new FormalParameter[]{});


            C_CLASS.forceSetBelongingClass(C_CLASS);
            C_OBJECT.forceSetBelongingClass(C_CLASS);

            C_CLASS.setExplicitInterfaces(I_TYPE);
            C_INTERFACE.setExplicitInterfaces(I_TYPE);
            try {
                C_CLASS.addInstanceMethod(new ToolMethod(m, ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, Visibility.Public,
                        ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Binary, "is"),
                        new FormalParameter[]{new FormalParameter(Memory.ARG_IDENTIFIER, C_OBJECT)},
                        mem -> new ToolBoolean(
                                mem,
                                ((ToolType) mem.getSelfObject()).isOperator(mem.getObjectByIdentifier(Memory.ARG_IDENTIFIER)))));
                C_CLASS.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        "canBeUsedAs",
                        new FormalParameter[]{new FormalParameter("other", I_TYPE)},
                        mem -> new ToolBoolean(
                                mem,
                                ((ToolType) mem.getSelfObject()).canBeUsedAs((ToolType) mem.getObjectByIdentifier("other")))));
                C_CLASS.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        "getConvertibility",
                        new FormalParameter[]{new FormalParameter("from", I_TYPE)},
                        mem -> new ToolInteger(
                                mem,
                                ((ToolType) mem.getSelfObject()).getConvertibility((ToolType) mem.getObjectByIdentifier("from"))
                        )));
                C_CLASS.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        "getObjectConvertibility",
                        new FormalParameter[]{new FormalParameter("from", C_OBJECT)},
                        mem -> new ToolInteger(
                                mem,
                                ((ToolType) mem.getSelfObject()).getObjectConvertibility((ToolObject) mem.getObjectByIdentifier("from"))
                        )));
                C_CLASS.addInstanceMethod(new ToolMethod(m, ToolGetterMethod.METHOD_CATEGORY_GETTER, Visibility.Public,
                        "name",
                        new FormalParameter[]{},
                        mem -> new ToolString(
                                mem,
                                ((ToolType) mem.getSelfObject()).getTypeName())
                ));

                C_INTERFACE.addInstanceMethod(new ToolMethod(m, ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, Visibility.Public,
                        ToolOperatorMethod.getOperatorMethodName(ToolOperatorMethod.Mode.Binary, "is"),
                        new FormalParameter[]{new FormalParameter(Memory.ARG_IDENTIFIER, C_OBJECT)},
                        mem -> new ToolBoolean(
                                mem,
                                ((ToolType) mem.getSelfObject()).isOperator(mem.getObjectByIdentifier(Memory.ARG_IDENTIFIER)))));
                C_INTERFACE.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        "canBeUsedAs",
                        new FormalParameter[]{new FormalParameter("other", I_TYPE)},
                        mem -> new ToolBoolean(
                                mem,
                                ((ToolType) mem.getSelfObject()).canBeUsedAs((ToolType) mem.getObjectByIdentifier("other")))));
                C_INTERFACE.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        " getConvertibility",
                        new FormalParameter[]{new FormalParameter("from", I_TYPE)},
                        mem -> new ToolInteger(
                                mem,
                                ((ToolType) mem.getSelfObject()).getConvertibility((ToolType) mem.getObjectByIdentifier("from"))
                        )));
                C_INTERFACE.addInstanceMethod(new ToolMethod(m, Visibility.Public,
                        "getObjectConvertibility",
                        new FormalParameter[]{new FormalParameter("from", C_OBJECT)},
                        mem -> new ToolInteger(
                                mem,
                                ((ToolType) mem.getSelfObject()).getObjectConvertibility((ToolObject) mem.getObjectByIdentifier("from"))
                        )));
                C_INTERFACE.addInstanceMethod(new ToolMethod(m, ToolGetterMethod.METHOD_CATEGORY_GETTER, Visibility.Public,
                        "name",
                        new FormalParameter[]{},
                        mem -> new ToolString(
                                mem,
                                ((ToolType) mem.getSelfObject()).getTypeName())
                ));


            } catch (AmbiguousMethodDefinitionException e) {
                e.printStackTrace();
            }

            C_INTERFACE.implementsInterface(I_TYPE);

            C_LIST.implementsInterface(I_ITERABLE);
            C_INTEGER_RANGE.implementsInterface(I_ITERABLE);

            NATIVE_CLASS_MAP = new MapBuilder<Class<?>, ToolClass>()
                    .put(ToolObject.class, C_OBJECT)
                    .put(ToolClass.class, C_CLASS)
                    .put(ToolInterface.class, C_INTERFACE)
                    .put(ToolExtensor.class, C_EXTENSOR)
                    .put(ToolMethod.class, C_METHOD)
                    .put(ToolTuple.class, C_TUPLE)
                    .put(ToolInteger.class, C_INTEGER)
                    .put(ToolString.class, C_STRING)
                    .put(ToolBoolean.class, C_BOOLEAN)
                    .put(ToolList.class, C_LIST)
                    .put(ToolIntegerRange.class, C_INTEGER_RANGE)
                    .put(ToolException.class, C_EXCEPTION)
                    .get();

            NATIVE_INTERFACE_MAP = new MapBuilder<Class<?>, ToolInterface>()
                    .put(ToolType.class, I_TYPE)
                    .get();

            for (Map.Entry<Class<?>, ToolClass> e : NATIVE_CLASS_MAP.entrySet()) {
                loadNativeClass(m, e.getKey(), e.getValue());
            }

            for (Map.Entry<Class<?>, ToolInterface> e : NATIVE_INTERFACE_MAP.entrySet()) {
                loadNativeInterface(m, e.getKey(), e.getValue());
            }
        } catch (NativeClassLoadFailedException | ToolNativeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void loadNativeInterface(Memory m, Class<?> nativeInterface, ToolInterface toolInterface)
            throws NativeClassLoadFailedException, ToolNativeException {
        loadNativeInterfaceMethods(m, nativeInterface, toolInterface);
    }


    public void loadNativeClass(Memory m, Class<?> nativeClass, ToolClass toolClass)
            throws NativeClassLoadFailedException, ToolNativeException {
        loadNativeMethods(m, nativeClass, toolClass);
        //loadNativeFields(nativeClass, toolClass);
    }


    public void loadNativeInterfaceMethods(Memory mem, Class<?> nativeInterface, ToolInterface toolInterface)
            throws NativeClassLoadFailedException, ToolNativeException {
        if (!nativeInterface.isInterface())
            throw new NativeClassLoadFailedException(nativeInterface.getName() + " is not a Java interface.");

        for (Method m : nativeInterface.getDeclaredMethods()) {
            if (m.isAnnotationPresent(NativeClassMethod.class) || m.isAnnotationPresent(NativeInstanceMethod.class)) {
                if (!m.isDefault()) {
                    Pair<ToolMethod, Boolean> toolMethodBooleanPair = loadNativeMethod(mem, m);
                    ToolMethod newMethod = toolMethodBooleanPair.getFirst();
                    Boolean isInstanceMethod = toolMethodBooleanPair.getSecond();
                    if(!isInstanceMethod){
                        toolInterface.addMethod(newMethod);
                    }else{
                        toolInterface.addDefaultMethod(newMethod);
                    }
                } else {
                    ToolMethodPrototype methodPrototype = loadInterfaceAbstractNativeMethod(mem, m);
                    toolInterface.addMethodDeclaration(methodPrototype);
                }
            }
        }
    }

    public void loadNativeMethods(Memory mem, Class<?> nativeClass, ToolClass toolClass)
            throws NativeClassLoadFailedException, ToolNativeException {
        for (Method m : nativeClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(NativeClassMethod.class) || m.isAnnotationPresent(NativeInstanceMethod.class)) {
                Pair<ToolMethod, Boolean> toolMethodBooleanPair = loadNativeMethod(mem, m);
                ToolMethod newMethod = toolMethodBooleanPair.getFirst();
                Boolean isInstanceMethod = toolMethodBooleanPair.getSecond();
                if (!isInstanceMethod) {
                    toolClass.addClassMethod(newMethod);
                } else {
                    toolClass.addInstanceMethod(newMethod);
                }
                Lol.v("Added native method: " + newMethod.getMethodCategory() + " " + (isInstanceMethod ?
                        newMethod.completeInstanceMethodName(toolClass) :
                        newMethod.completeClassMethodName(toolClass)));


            }
        }
    }

    public ToolMethodPrototype loadInterfaceAbstractNativeMethod(Memory mem, Method m) throws NativeClassLoadFailedException {
        if (!ToolObject.class.isAssignableFrom(m.getReturnType())) //it must return a ToolObject or derivate
            throw new NativeClassLoadFailedException();

        if(m.isAnnotationPresent(NativeClassMethod.class))
            throw new NativeClassLoadFailedException(
                    "an interface abstract method cannot be annotated as NativeClassMethod");

        if(!m.isAnnotationPresent(NativeClassMethod.class))
            throw new NativeClassLoadFailedException("tried to load a not-annotated native abstract method");

        NativeInstanceMethod instanceAnn = m.getDeclaredAnnotation(NativeInstanceMethod.class);
        List<Parameter> nativeParameters = PJ.list(m.getParameters());

        if(nativeParameters.isEmpty() || !Memory.class.isAssignableFrom(nativeParameters.get(0).getType()))
            throw new NativeClassLoadFailedException("first parameter of converted method must always be of type Memory");

        List<ToolType> parTypes = new ArrayList<>();
        for(int i = 1; i < nativeParameters.size(); ++i){
            Parameter nativePar = nativeParameters.get(i);
            Class nativeParType = nativePar.getType();
            ToolType baseType = getBaseType(nativeParType);
            parTypes.add(baseType);
        }

        return new ToolMethodPrototype(mem,
                instanceAnn.category(),
                (instanceAnn.category()).equals(ToolOperatorMethod.METHOD_CATEGORY_OPERATOR) ?
                        (ToolOperatorMethod.getOperatorMethodName(instanceAnn.mode(), instanceAnn.value())) :
                        (instanceAnn.value().equals("")?m.getName():instanceAnn.value()),
                parTypes);

    }

    public Pair<ToolMethod, Boolean> loadNativeMethod(Memory mem, Method m) throws NativeClassLoadFailedException {
        if (!ToolObject.class.isAssignableFrom(m.getReturnType())) //it must return a ToolObject or derivate
            throw new NativeClassLoadFailedException();

        if(!(m.isAnnotationPresent(NativeClassMethod.class) || m.isAnnotationPresent(NativeInstanceMethod.class)))
            throw new NativeClassLoadFailedException("tried to load a not-annotated native method");


        NativeClassMethod classAnn = m.getDeclaredAnnotation(NativeClassMethod.class);
        NativeInstanceMethod instanceAnn = m.getDeclaredAnnotation(NativeInstanceMethod.class);
        boolean isInstanceMethod = instanceAnn != null;

        if(isInstanceMethod && Modifier.isStatic(m.getModifiers()) || !isInstanceMethod && !Modifier.isStatic(m.getModifiers()))
            throw new NativeClassLoadFailedException("tried to load a method annotated with wrong staticity: "+m.getName());


        List<Parameter> nativeParameters = PJ.list(m.getParameters());
        if(nativeParameters.isEmpty() || !Memory.class.isAssignableFrom(nativeParameters.get(0).getType()))
            throw new NativeClassLoadFailedException("first parameter of converted method must always be of type Memory");

        if(!isInstanceMethod && (!(nativeParameters.size() >= 2) ||
                !nativeParameters.get(1).isAnnotationPresent(SelfParameter.class)))
            throw new NativeClassLoadFailedException("when method is static, second parameter must be the self object");

        int effectiveParsIndexStart = isInstanceMethod ? 1 : 2;

        List<FormalParameter> parameters = new ArrayList<>();
        for(int i = effectiveParsIndexStart; i < nativeParameters.size(); ++i){
            Parameter nativePar = nativeParameters.get(i);
            Class nativeParType = nativePar.getType();
            ToolType baseType = getBaseType(nativeParType);
            parameters.add(new FormalParameter(nativePar.getName(), baseType));
        }

        ToolMethod newMethod = new ToolMethod(
                mem,
                isInstanceMethod ? instanceAnn.category() : classAnn.category(),
                isInstanceMethod ? instanceAnn.visibility() : classAnn.visibility(),
                (isInstanceMethod ? instanceAnn.category() : classAnn.category()).equals(
                        ToolOperatorMethod.METHOD_CATEGORY_OPERATOR) ?
                        (ToolOperatorMethod.getOperatorMethodName(
                                isInstanceMethod ? instanceAnn.mode() : classAnn.mode(),
                                isInstanceMethod ? instanceAnn.value() : classAnn.value())
                        ) : (isInstanceMethod ?
                                (instanceAnn.value().equals("")?m.getName():instanceAnn.value())
                                : classAnn.value().equals("")?m.getName():classAnn.value()),
                parameters.toArray(new FormalParameter[parameters.size()]),
                memory -> {
                    List<ToolObject> actualPars = new ArrayList<>();
                    for(FormalParameter par : parameters){
                        ToolObject x = memory.getObjectByIdentifier(par.getParameterName());
                        if(par.getParameterType().isOperator(x)) {
                            actualPars.add(x);
                        }else throw new BadMethodCallException(mem,
                                "Something went wrong while attempting to call a native method"); //TODO specify what method
                    }
                    try {
                        if(isInstanceMethod) {
                            List<Object> actualNativePars = new ArrayList<>();
                            actualNativePars.add(memory);
                            actualNativePars.addAll(actualPars);
                            Lol.v("calling native method named '"+m.getName()+"' with no. of parameters: "+actualNativePars.size());
                            return (ToolObject) m.invoke(memory.getSelfObject(),
                                    actualNativePars.toArray(new Object[actualNativePars.size()]));
                        }else{
                            List<Object> actualNativePars = new ArrayList<>();
                            actualNativePars.add(memory);
                            actualNativePars.add(memory.getSelfObject());
                            actualNativePars.addAll(actualPars);
                            return (ToolObject) m.invoke(null, actualNativePars.toArray(new Object[actualNativePars.size()]));
                        }
                    }catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("InvokeFailed");
                    }
                });
        return new Pair<>(newMethod, isInstanceMethod);
    }

    public ToolType getBaseType(Class<?> nativeClass) throws NativeClassLoadFailedException {
        ToolType baseType;
        if(!nativeClass.isInterface()){
            baseType = NATIVE_CLASS_MAP.get(nativeClass);
        }else{
            baseType = NATIVE_INTERFACE_MAP.get(nativeClass);
        }
        if(baseType == null) throw new NativeClassLoadFailedException(
                "no corresponding base type found for: "+nativeClass.getName());
        return baseType;
    }

    //TODO: convert native java objects

    public static class NativeClassLoadFailedException extends Exception {
        public NativeClassLoadFailedException() {
        }

        public NativeClassLoadFailedException(String msg) {
            super(msg);
        }
    }

    //todo visibility of fields
    //todo visibility of methods
    //todo better object id type, not Integer please
}
