package com.parsleyj.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class ToolClass extends ToolObject {
    private final String className;
    private final ToolClass parentClass;
    private MethodTable instanceMethodTable = new MethodTable();
    private MethodTable classMethodTable = new MethodTable();
    private Map<String, Field> fieldMap = new HashMap<>();

    public ToolClass(String className, ToolClass parentClass) {
        super(BaseTypes.C_CLASS);
        this.className = className;
        this.parentClass = parentClass;
    }


    public ToolMethod findInstanceMethod(String name, List<ToolClass> argumentTypes) {
        ToolMethod result = instanceMethodTable.resolve(name, argumentTypes);
        if (result == null && parentClass != null) return parentClass.findInstanceMethod(name, argumentTypes);
        return result;
    }

    public ToolMethod findClassMethod(String name, List<ToolClass> argumentTypes) {
        return classMethodTable.resolve(name, argumentTypes);
    }

    public String getClassName() {
        return className;
    }

    public ToolClass getParentClass() {
        return parentClass;
    }

    public void addInstanceMethod(ToolMethod tm) {
        getInstanceMethodTable().add(tm);
    }

    public void addClassMethod(ToolMethod tm) {
        getClassMethodTable().add(tm);
    }

    public void addInstanceMethods(List<ToolMethod> tms){
        for(ToolMethod tm:tms){
            addInstanceMethod(tm);
        }
    }

    public void addClassMethods(List<ToolMethod> tms){
        for(ToolMethod tm:tms){
            addClassMethod(tm);
        }
    }

    public MethodTable getInstanceMethodTable() {
        return instanceMethodTable;
    }

    public MethodTable getClassMethodTable() {
        return classMethodTable;
    }

    public Map<String, Field> getFields() {
        return fieldMap;
    }

    public boolean isOrExtends(ToolClass cBoolean) {
        ToolClass tmp = cBoolean;
        while (tmp != null){
            if(Objects.equals(this.getId(), tmp.getId())) return true;
            tmp = tmp.getParentClass();
        }
        return false;
    }

    @Override
    public String toString() {
        return "<CLASS:"+className+">";
    }
}
