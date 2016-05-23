package com.parsleyj.tool.objects;

import com.parsleyj.tool.ToolBlock;
import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.exceptions.BadMethodCallException;
import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.annotations.fields.ClassField;
import com.parsleyj.tool.objects.annotations.fields.InstanceField;
import com.parsleyj.tool.objects.annotations.fields.Property;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeClassMethod;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.*;
import com.parsleyj.tool.objects.exception.ToolException;
import com.parsleyj.tool.objects.exception.ToolExceptionClass;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.Visibility;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.objects.method.special.ToolSetterMethod;
import com.parsleyj.utils.Lol;
import com.parsleyj.utils.MapBuilder;
import com.parsleyj.utils.PJ;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

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
    // --- BASE CLASSES ---
    public static final ToolClass C_OBJECT = new ToolClass("Object", null);
    public static final ToolClass C_CLASS = new ToolClass("Class", C_OBJECT);
    public static final ToolClass C_INTERFACE = new ToolClass("Interface", C_OBJECT);
    public static final ToolClass C_TOOL = new ToolClass("Tool", C_OBJECT);//TODO this must be an object
    public static final ToolClass C_FIELD = new ToolClass("ToolField", C_OBJECT);
    public static final ToolClass C_BLOCK = new ToolClass("ScopedBlock", C_OBJECT);
    public static final ToolClass C_METHOD = new ToolClass("Method", C_OBJECT);
    public static final ToolClass C_METHOD_CALL = new ToolClass("MethodCall", C_OBJECT);
    public static final ToolClass C_INTEGER = new ToolClass("Integer", C_OBJECT);
    public static final ToolClass C_STRING = new ToolClass("String", C_OBJECT);
    public static final ToolClass C_BOOLEAN = new ToolClass("Boolean", C_OBJECT);
    public static final ToolClass C_LIST = new ToolClass("List", C_OBJECT);
    public static final ToolClass C_INTEGER_RANGE = new ToolClass("IntegerRange", C_OBJECT);
    public static final ToolClass C_EXCEPTION = new ToolClass("Exception", C_OBJECT);

    // --- EXCEPTIONS ---
    public static final ToolExceptionClass C_REFERENCE_ALREADY_EXISTS_EXCEPTION = new ToolExceptionClass("ReferenceAlreadyExistsException");
    public static final ToolExceptionClass C_REFERENCE_NOT_FOUND_EXCEPTION = new ToolExceptionClass("ReferenceNotFoundException");
    public static final ToolExceptionClass C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION = new ToolExceptionClass("InvalidConditionalExpressionException");
    public static final ToolExceptionClass C_BAD_METHOD_CALL_EXCEPTION = new ToolExceptionClass("BadMethodCallException");
    public static final ToolExceptionClass C_AMBIGUOUS_METHOD_CALL_EXCEPTION = new ToolExceptionClass("AmbiguousMethodCallException");
    public static final ToolExceptionClass C_AMBIGUOUS_METHOD_DEFINITION_EXCEPTION = new ToolExceptionClass("AmbiguousMethodDefinitionException");
    public static final ToolExceptionClass C_METHOD_NOT_FOUND_EXCEPTION = new ToolExceptionClass("MethodNotFoundException");
    public static final ToolExceptionClass C_CALL_ON_NULL_EXCEPTION = new ToolExceptionClass("CallOnNullException");
    public static final ToolExceptionClass C_ARITHMETIC_EXCEPTION = new ToolExceptionClass("ArithmeticException");
    public static final ToolExceptionClass C_INVALID_INDEX_TYPE_EXCEPTION = new ToolExceptionClass("InvalidIndexTypeException");
    public static final ToolExceptionClass C_INVALID_INDEX_LIST_EXCEPTION = new ToolExceptionClass("InvalidIndexListException");
    public static final ToolExceptionClass C_INVALID_ITERABLE_EXPRESSION_EXCEPTION = new ToolExceptionClass("InvalidIterableExpressionException");
    public static final ToolExceptionClass C_INVALID_PARAMETER_TYPE_EXCEPTION = new ToolExceptionClass("InvalidParameterTypeException");
    public static final ToolExceptionClass C_NAME_ALREADY_USED_EXCEPTION = new ToolExceptionClass("NameAlreadyUsedException");
    public static final ToolExceptionClass C_VISIBILITY_EXCEPTION = new ToolExceptionClass("VisibilityException");
    public static final ToolExceptionClass C_INDEX_OUT_OF_BOUNDS_EXCEPTION = new ToolExceptionClass("IndexOutOfBoundsException");
    public static final ToolExceptionClass C_NAME_NOT_FOUND_EXCEPTION = new ToolExceptionClass("NameNotFoundException");

    // --- INTERFACES ---
    public static final ToolInterface I_ITERABLE = new ToolInterface("Iterable", Collections.emptyList())
            .addMethodDeclaration(
                    ToolGetterMethod.METHOD_CATEGORY_GETTER,
                    "iterator",
                    new FormalParameter[]{});


    public static final ToolInterface I_ITERATOR = new ToolInterface("Iterator", Collections.emptyList())
            .addMethodDeclaration(
                    ToolGetterMethod.METHOD_CATEGORY_GETTER,
                    "hasNext",
                    new FormalParameter[]{})
            .addMethodDeclaration(
                    ToolGetterMethod.METHOD_CATEGORY_GETTER,
                    "next",
                    new FormalParameter[]{});
    public static final Map<Class<?>, ToolClass> NATIVE_CLASS_MAP = new MapBuilder<Class<?>, ToolClass>()
            .put(ToolObject.class, C_OBJECT)
            .put(ToolClass.class, C_CLASS)
            .put(ToolInterface.class, C_INTERFACE)
            .put(ToolField.class, C_FIELD)
            .put(ToolBlock.class, C_BLOCK)
            .put(ToolMethod.class, C_METHOD)
            .put(ToolInteger.class, C_INTEGER)
            .put(ToolString.class, C_STRING)
            .put(ToolBoolean.class, C_BOOLEAN)
            .put(ToolList.class, C_LIST)
            .put(ToolIntegerRange.class, C_INTEGER_RANGE)
            .put(ToolException.class, C_EXCEPTION)
            .get();

    public static List<ToolClass> getAllBaseClasses() {
        return Arrays.asList(
                C_OBJECT,
                C_CLASS,
                C_INTERFACE,
                C_TOOL,
                C_FIELD,
                C_BLOCK,
                C_METHOD,
                C_METHOD_CALL,
                C_INTEGER,
                C_STRING,
                C_BOOLEAN,
                C_LIST,
                C_INTEGER_RANGE,
                C_EXCEPTION,

                C_REFERENCE_ALREADY_EXISTS_EXCEPTION,
                C_REFERENCE_NOT_FOUND_EXCEPTION,
                C_INVALID_CONDITIONAL_EXPRESSION_EXCEPTION,
                C_BAD_METHOD_CALL_EXCEPTION,
                C_AMBIGUOUS_METHOD_CALL_EXCEPTION,
                C_METHOD_NOT_FOUND_EXCEPTION,
                C_CALL_ON_NULL_EXCEPTION,
                C_ARITHMETIC_EXCEPTION,
                C_INVALID_INDEX_TYPE_EXCEPTION,
                C_INVALID_INDEX_LIST_EXCEPTION,
                C_INDEX_OUT_OF_BOUNDS_EXCEPTION,
                C_INVALID_ITERABLE_EXPRESSION_EXCEPTION,
                C_INVALID_PARAMETER_TYPE_EXCEPTION,
                C_NAME_ALREADY_USED_EXCEPTION,
                C_VISIBILITY_EXCEPTION,
                C_NAME_NOT_FOUND_EXCEPTION
        );
    }

    public static List<ToolInterface> getAllBaseInterfaces() {
        return Arrays.asList(
                I_ITERABLE,
                I_ITERATOR
        );
    }

    static {
        C_OBJECT.forceSetBelongingClass(C_CLASS);
        C_CLASS.forceSetBelongingClass(C_CLASS);

        C_LIST.implementsInterface(I_ITERABLE);
        C_INTEGER_RANGE.implementsInterface(I_ITERABLE);

        try {
            C_TOOL.addClassMethod(new ToolMethod(
                    Visibility.Public,
                    "print",
                    new FormalParameter[]{
                            new FormalParameter("this", C_CLASS)
                    },
                    new FormalParameter[]{
                            new FormalParameter("x", C_OBJECT)
                    }, memory -> {
                ToolObject x = memory.getObjectByIdentifier("x");
                if (x.getBelongingClass().isOrExtends(C_OBJECT)) {
                    System.out.println(x.getPrintString());
                    return x;
                } else
                    throw new BadMethodCallException("Something went wrong while attempting to call Tool.print(Object)");
            }
            ));

            for (Map.Entry<Class<?>, ToolClass> e : NATIVE_CLASS_MAP.entrySet()) {
                loadNativeMembers(e.getKey(), e.getValue());
            }
        } catch (AmbiguousMethodDefinitionException | NativeClassLoadFailedException e) {
            e.printStackTrace();
        }


    }


    public static void loadNativeMembers(Class<?> nativeClass, ToolClass toolClass) throws NativeClassLoadFailedException, AmbiguousMethodDefinitionException {
        loadNativeMethods(nativeClass, toolClass);
        loadNativeFields(nativeClass, toolClass);
    }

    public static void loadNativeFields(Class<?> nativeClass, ToolClass toolClass) throws NativeClassLoadFailedException, AmbiguousMethodDefinitionException {
        for (Field f : nativeClass.getFields()) {
            if (f.isAnnotationPresent(ClassField.class)) {
                /*try {
                    Object nativeVal = f.get(null);
                    ToolClass baseType = NATIVE_CLASS_MAP.get(f.getType());
                    if (baseType == null) throw new NativeClassLoadFailedException();
                    toolClass.addClassField(new Reference(f.getMethodName()));
                } catch (IllegalAccessException e) {
                    throw new NativeClassLoadFailedException();
                }*/
            } else if (f.isAnnotationPresent(InstanceField.class)) {
                ToolClass baseType = NATIVE_CLASS_MAP.get(f.getType());
                if (baseType == null) throw new NativeClassLoadFailedException();
                toolClass.addInstanceField(new ToolField(baseType, f.getName()));
            } else if (f.isAnnotationPresent(Property.class)) {
                ToolClass baseType = NATIVE_CLASS_MAP.get(f.getType());
                if (baseType == null) throw new NativeClassLoadFailedException();
                toolClass.addInstanceField(new ToolField(baseType, f.getName()));
                Property p = f.getAnnotation(Property.class);
                switch (p.value()) {
                    case ReadOnly: {
                        toolClass.addInstanceMethod(createPropertyGetter(toolClass, f));
                        break;
                    }
                    case ReadWrite: {
                        toolClass.addInstanceMethod(createPropertyGetter(toolClass, f));
                        toolClass.addInstanceMethod(createPropertySetter(toolClass, baseType, f));
                        break;
                    }
                    case WriteOnly: {
                        toolClass.addInstanceMethod(createPropertySetter(toolClass, baseType, f));
                        break;
                    }
                }
            }
        }
    }


    public ToolObject convertNativeObject(Object x, Memory m) throws IllegalAccessException, ReferenceAlreadyExistsException {
        if (x == null) return BaseTypes.O_NULL;
        if (x instanceof Integer) {
            return new ToolInteger((Integer) x);
        } else if (x instanceof Boolean) {
            return new ToolBoolean((Boolean) x);
        } else if (x instanceof String) {
            return new ToolString((String) x);
        }
        ToolClass type = NATIVE_CLASS_MAP.get(x.getClass());
        ToolObject result = type.newInstance();
        for (Field field : x.getClass().getFields()) {
            result.addReferenceMember(m.newLocalReference(field.getName(), convertNativeObject(field.get(x), m)));
        }
        return result;
    }

    @NotNull
    private static ToolGetterMethod createPropertyGetter(ToolClass selfType, Field f) {
        return new ToolGetterMethod(f.getName(), selfType, memory -> {
            ToolObject self = memory.getObjectByIdentifier(Memory.SELF_IDENTIFIER);
            Reference r = self.getReferenceMember(f.getName());
            return memory.getObjectById(r.getPointedId());
        });
    }

    @NotNull
    private static ToolSetterMethod createPropertySetter(ToolClass selfType, ToolClass argType, Field f) {
        return new ToolSetterMethod(f.getName(), selfType, argType, memory -> {
            ToolObject self = memory.getObjectByIdentifier(Memory.SELF_IDENTIFIER);
            ToolObject arg = memory.getObjectByIdentifier(Memory.ARG_IDENTIFIER);
            Reference r = self.getReferenceMember(f.getName());
            memory.updateReference(r, arg);
            return arg;
        });
    }

    public static void loadNativeMethods(Class<?> nativeClass, ToolClass toolClass) throws NativeClassLoadFailedException, AmbiguousMethodDefinitionException {
        for (Method m : nativeClass.getMethods()) {
            if (m.isAnnotationPresent(NativeClassMethod.class) || m.isAnnotationPresent(NativeInstanceMethod.class)) {
                if (!ToolObject.class.isAssignableFrom(m.getReturnType())) //it must return a ToolObject or derivate
                    throw new NativeClassLoadFailedException();


                NativeClassMethod classAnn = m.getDeclaredAnnotation(NativeClassMethod.class);
                NativeInstanceMethod instanceAnn = m.getDeclaredAnnotation(NativeInstanceMethod.class);
                boolean isInstanceMethod = instanceAnn != null;
                Parameter[] nativePars = m.getParameters();
                List<FormalParameter> parameters = new ArrayList<>();
                List<FormalParameter> implicitParameters = new ArrayList<>();
                boolean hasSelfParameter = false;
                boolean hasMemoryParameter = false;
                for (Parameter nativePar : nativePars) {
                    if (!nativePar.isAnnotationPresent(MemoryParameter.class)) {
                        if (ToolObject.class.isAssignableFrom(nativePar.getType())) {
                            ToolClass baseType = NATIVE_CLASS_MAP.get(nativePar.getType());
                            if (baseType == null) throw new NativeClassLoadFailedException();

                            if (nativePar.isAnnotationPresent(ImplicitParameter.class)) {
                                ImplicitParameter implicitParAnn = nativePar.getDeclaredAnnotation(ImplicitParameter.class);
                                if (implicitParAnn.value().equals(Memory.SELF_IDENTIFIER))
                                    hasSelfParameter = true;
                                implicitParameters.add(new FormalParameter(implicitParAnn.value(), baseType));
                            } else {
                                parameters.add(new FormalParameter(nativePar.getName(), baseType));
                            }
                        } else {
                            throw new NativeClassLoadFailedException();
                        }
                    }else{
                        hasMemoryParameter = true;
                    }
                }
                if (isInstanceMethod && !hasSelfParameter) throw new NativeClassLoadFailedException();

                try {
                    boolean finalHasMemoryParameter = hasMemoryParameter;
                    ToolMethod newMethod = new ToolMethod(
                            isInstanceMethod ? instanceAnn.category() : classAnn.category(),
                            isInstanceMethod ? instanceAnn.visibility() : classAnn.visibility(),
                            (isInstanceMethod ? instanceAnn.category() : classAnn.category()).equals(ToolOperatorMethod.METHOD_CATEGORY_OPERATOR) ?
                                    (ToolOperatorMethod.getOperatorMethodName(
                                            isInstanceMethod ? instanceAnn.mode() : classAnn.mode(),
                                            isInstanceMethod ? instanceAnn.value() : classAnn.value())
                                    ) : (isInstanceMethod ? instanceAnn.value() : classAnn.value()),
                            implicitParameters.toArray(new FormalParameter[implicitParameters.size()]),
                            parameters.toArray(new FormalParameter[parameters.size()]),
                            memory -> {
                                List<ToolObject> actualPars = new ArrayList<>();
                                List<ToolObject> implicitPars = new ArrayList<>();
                                for (FormalParameter par : implicitParameters) {
                                    ToolObject x = memory.getObjectByIdentifier(par.getParameterName());
                                    if (x.getBelongingClass().isOrExtends(par.getParameterType())) {
                                        implicitPars.add(x);
                                    } else
                                        throw new BadMethodCallException("Something went wrong while attempting to call a native method"); //TODO specify what method
                                }
                                for (FormalParameter par : parameters) {
                                    ToolObject x = memory.getObjectByIdentifier(par.getParameterName());
                                    if (x.getBelongingClass().isOrExtends(par.getParameterType())) {
                                        actualPars.add(x);
                                    } else
                                        throw new BadMethodCallException("Something went wrong while attempting to call a native method"); //TODO specify what method
                                }
                                try {
                                    if (finalHasMemoryParameter)
                                        return (ToolObject) m.invoke(null, PJ.tempConcat(Collections.singletonList(memory), PJ.tempConcatFlex(implicitPars, actualPars)).toArray(new Object[implicitPars.size() + actualPars.size() + 1]));
                                    else
                                        return (ToolObject) m.invoke(null, PJ.tempConcatFlex(implicitPars, actualPars).toArray(new Object[implicitPars.size() + actualPars.size()]));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException("InvokeFailed");
                                }
                            });
                    if (!isInstanceMethod) {
                        toolClass.addClassMethod(newMethod);
                    } else {
                        toolClass.addInstanceMethod(newMethod);
                    }
                    Lol.v("Added native method: "+ newMethod.getMethodCategory()+ " " + (isInstanceMethod ?
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
    //todo reference types?
    //todo better object id type, not Integer please
    //todo generic this/self pointer
    //todo liskov
    //todo list types
    //todo in method definitions, use a "isOfTypeOrExtendingType(ToolClass)" method

}
