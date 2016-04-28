package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodCallException;

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
    private Map<String, ToolField> fieldMap = new HashMap<>();

    public ToolClass(String className, ToolClass parentClass) {
        super(BaseTypes.C_CLASS);
        this.className = className;
        this.parentClass = parentClass;
    }


    public ToolMethod findInstanceMethod(String name, List<ToolClass> argumentTypes) throws AmbiguousMethodCallException {
        ToolMethod result = instanceMethodTable.resolve(name, argumentTypes);
        if (result == null && parentClass != null) return parentClass.findInstanceMethod(name, argumentTypes);
        return result;
    }

    public ToolMethod findClassMethod(String name, List<ToolClass> argumentTypes) throws AmbiguousMethodCallException {
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

    public int getConvertibility(ToolClass c){
        ToolClass tmp = c;
        int points = 0;
        while (tmp != c || !Objects.equals(tmp.getId(), c.getId())){
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
}
