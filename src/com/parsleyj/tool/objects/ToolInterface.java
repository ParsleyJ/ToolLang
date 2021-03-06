package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.ToolMethodPrototype;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 15/05/16.
 * TODO: javadoc
 */
public class ToolInterface extends ToolObject implements ToolType {

    private final String interfaceName;
    private final List<ToolInterface> parentInterfaces;
    private List<ToolMethodPrototype> instanceMethods = new ArrayList<>();
    private List<ToolMethod> defaultInstanceMethods = new ArrayList<>();

    public ToolInterface(Memory m, String interfaceName, List<ToolInterface> parentInterfaces) {
        super(m, m.baseTypes().C_INTERFACE);
        this.interfaceName = interfaceName;
        this.parentInterfaces = parentInterfaces;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public List<ToolInterface> getParentInterfaces() {
        return parentInterfaces;
    }

    public List<ToolMethodPrototype> getDeclaredInstanceMethods() {
        return instanceMethods;
    }

    public void addParentInterface(ToolInterface toolInterface) {
        parentInterfaces.add(toolInterface);
    }

    public ToolInterface addMethodDeclaration(Memory m, String methodCategory, String name, FormalParameter[] formalParameters){
        this.instanceMethods.add(new ToolMethodPrototype(
                m,
                methodCategory,
                name,
                Arrays.asList(formalParameters).stream()
                        .map(FormalParameter::getParameterType)
                        .collect(Collectors.toList())
        ));
        return this;
    }

    public ToolInterface addMethodDeclaration(ToolMethodPrototype prototype){
        this.instanceMethods.add(prototype);
        return this;
    }

    public ToolInterface addDefaultMethod(ToolMethod method){
        this.defaultInstanceMethods.add(method);
        return this;
    }

    public List<ToolMethod> getDefaultInstanceMethods(){
        return defaultInstanceMethods;
    }

    public boolean isOrExtends(ToolInterface ti){
        if(ti == null) return false;
        if(Objects.equals(this.getId(), ti.getId())) return true;
        if(parentInterfaces.isEmpty()) return false;

        for(ToolInterface parent: parentInterfaces){
            if(parent.isOrExtends(ti)) return true;
        }
        return false;
    }

    public List<ToolMethodPrototype> getInstanceMethods(){
        List<ToolMethodPrototype> result = new ArrayList<>();
        result.addAll(instanceMethods);
        for(ToolInterface ti: parentInterfaces){
            result.addAll(ti.getInstanceMethods());
        }
        return result;
    }

    private int interfaceConvertibility(ToolInterface to){
        ToolInterface from = this;

        if(Objects.equals(this.getId(), from.getId())) return 0;

        for(ToolInterface parent: parentInterfaces){
            if(parent.isOrExtends(to)) {
                int parentConv = parent.interfaceConvertibility(to);
                return parentConv+1<0?Integer.MAX_VALUE : parentConv+1;
            }
        }

        return Integer.MAX_VALUE;
    }

    private int getExplicitConvertibility(ToolType from){
        if(from instanceof ToolClass){
            int min = Integer.MAX_VALUE;
            ToolClass klass = (ToolClass) from;
            for(ToolInterface i : klass.getExplicitDeclaredInterfaces()){
                min = Math.min(min, getExplicitConvertibility(i));
            }
            if(min == Integer.MAX_VALUE || min < 0) return Integer.MAX_VALUE;
            return min + 1;
        }else if(from instanceof ToolInterface){
            return ((ToolInterface) from).interfaceConvertibility(this);
        }else return Integer.MAX_VALUE;
    }

    @Override
    @NativeInstanceMethod(value = "typeName", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public String getTypeName() {
        return interfaceName;
    }

    @Override
    @NativeInstanceMethod(value = "is", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.Binary)
    public boolean isOperator(ToolObject o) throws ToolNativeException {
        return o.respondsToInterface(this);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    @NativeInstanceMethod
    public boolean canBeUsedAs(ToolType other) {
        if(other instanceof ToolInterface){
            return this.isOrExtends((ToolInterface) other);
        }else return false;
    }

    @Override
    @NativeInstanceMethod
    public int getObjectConvertibility(ToolObject from) throws ToolNativeException {
        if(from.getBelongingClass().implementsInterface(this)){
            return getExplicitConvertibility(from.getBelongingClass());
        }
        if(from.respondsToInterface(this)){
            return Integer.MAX_VALUE-10000-this.instanceMethods.size()*10;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    @NativeInstanceMethod
    public int getConvertibility(ToolType from) {
        return getExplicitConvertibility(from);
    }
}
