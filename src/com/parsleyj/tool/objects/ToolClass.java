package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.basetypes.ToolList;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.*;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolClass extends ToolObject {
    private final String className;
    private final ToolClass parentClass;
    private MethodTable instanceMethods = new MethodTable();
    private Map<String, ToolField> fieldMap = new HashMap<>();
    private Map<String, Memory.NameKind> nameTable = new HashMap<>();
    private List<ToolInterface> explicitInterfaces;

    public ToolClass(String className, ToolClass parentClass, ToolInterface... explicitInterfaces) {
        super(BaseTypes.C_CLASS);
        this.className = className;
        this.parentClass = parentClass;
        this.explicitInterfaces = Arrays.asList(explicitInterfaces);
    }


    public String getClassName() {
        return className;
    }

    public ToolClass getParentClass() {
        return parentClass;
    }

    public void addInstanceMethod(ToolMethod tm) throws AmbiguousMethodDefinitionException {
        getInstanceMethods().add(tm);
    }

    public void addClassMethod(ToolMethod tm) throws AmbiguousMethodDefinitionException {
        getClassMethodTable().add(tm);
    }

    public void addInstanceMethods(List<ToolMethod> tms) throws AmbiguousMethodDefinitionException {
        for(ToolMethod tm:tms){
            addInstanceMethod(tm);
        }
    }

    public void addClassMethods(List<ToolMethod> tms) throws AmbiguousMethodDefinitionException {
        for(ToolMethod tm:tms){
            addClassMethod(tm);
        }
    }

    public Map<String, Memory.NameKind> getNameTable() {
        return nameTable;
    }

    public void setNameTable(Map<String, Memory.NameKind> nameTable) {
        this.nameTable = nameTable;
    }

    public void addInstanceField(ToolField field){
        this.fieldMap.put(field.getIdentifier(), field);
    }

    public void addClassField(Reference reference){
        this.addReferenceMember(reference);
    }

    public MethodTable getInstanceMethods() {
        return instanceMethods;
    }

    public MethodTable getClassMethodTable() {
        return getMembersScope().getMethods();
    }

    public Map<String, ToolField> getFields() {
        return fieldMap;
    }

    public boolean isOrExtends(ToolClass type) {
        ToolClass tmp = this;
        while (tmp != null){
            if(Objects.equals(type.getId(), tmp.getId())) return true;
            tmp = tmp.getParentClass();
        }
        return false;
    }

    public int getConvertibility(ToolClass toBeConverted){
        ToolClass tmp = toBeConverted;
        int points = 0;
        while (this != tmp || !Objects.equals(this.getId(), tmp.getId())){
            ++points;
            tmp = tmp.getParentClass();
        }
        //TODO: consider user-defined conversions
        return points;
    }

    public boolean canBeConvertedTo(ToolClass type){
        return isOrExtends(type);
        //TODO: consider user-defined conversions
    }


    @Override
    public String toString() {
        return "<CLASS:"+className+">";
    }

    public ToolObject newInstance(Memory memory) {
        ToolObject newInstance = new ToolObject(this);
        for(ToolField f: fieldMap.values()){
            newInstance.writeObjectMember(f.getIdentifier(), memory, f.getDefaultValue());
        }
        newInstance.getMembersScope().setNameTable(new HashMap<>(nameTable));
        return newInstance;
    }

    public MethodTable generateInstanceCallableMethodTable() {
        if(this.getParentClass() == null || this == this.getParentClass()){
            return getInstanceMethods();
        }else{
            return getParentClass().generateInstanceCallableMethodTable().extend(getInstanceMethods());
        }
    }

    public List<ToolInterface> getExplicitDeclaredInterfaces() {
        return explicitInterfaces;
    }

    public void setExplicitInterfaces(ToolInterface... explicitInterfaces) {
        this.explicitInterfaces = Arrays.asList(explicitInterfaces);
    }

    public boolean implementsInterface(ToolInterface toolInterface) {
        return explicitImplements(toolInterface) || implicitImplements(toolInterface);
    }

    public boolean explicitImplements(ToolInterface toolInterface) {
        for (ToolInterface explicitInterface : explicitInterfaces) {
            if (Objects.equals(explicitInterface.getId(), toolInterface.getId())) return true;
        }
        return this.getParentClass() != null && this.getParentClass() != this && this.getParentClass().explicitImplements(toolInterface);
    }

    public boolean implicitImplements(ToolInterface toolInterface){
        MethodTable callables = generateInstanceCallableMethodTable();
        for(ToolMethod m: toolInterface.getInstanceMethods()){
            if(!callables.contains(m.getMethodCategory(), m.getMethodName(), m.getArgumentTypes())) return false;
        }
        return true;
    }

    @NativeInstanceMethod(value = "()", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.BinaryParametric)
    public static ToolObject roundBrackets(@MemoryParameter Memory memory, @ImplicitParameter ToolClass self, ToolList arg) throws MethodNotFoundException {
        if(arg.getToolObjects().isEmpty()){ //TODO: search for ctors
            return self.newInstance(memory);
        }
        throw new MethodNotFoundException("Constructor not found"); //todo better error
    }
}
