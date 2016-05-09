package com.parsleyj.tool.objects.classes;

import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.memory.Reference;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;

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
    private MethodTable instanceMethods = new MethodTable();
    private Map<String, ToolField> fieldMap = new HashMap<>();

    public ToolClass(String className, ToolClass parentClass) {
        super(BaseTypes.C_CLASS);
        this.className = className;
        this.parentClass = parentClass;
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
        return thisMethodTable;
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
