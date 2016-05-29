package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Giuseppe on 29/05/16.
 * TODO: javadoc
 */
public class ToolExtensor extends ToolObject {

    private MethodTable instanceMethods;
    private MethodTable ctors;
    private Map<String, ToolField> fieldMap = new HashMap<>();
    private HashMap<String, Memory.NameKind> nameTable = new HashMap<>();

    public ToolExtensor(Memory m) {
        super(m, m.baseTypes().C_EXTENSOR);
        instanceMethods = new MethodTable(m);
        ctors = new MethodTable(m);
    }

    public void addInstanceField(ToolField field){
        this.fieldMap.put(field.getIdentifier(), field);
    }

    public Map<String, ToolField> getFieldMap() {
        return fieldMap;
    }

    public HashMap<String, Memory.NameKind> getNameTable() {
        return nameTable;
    }

    public MethodTable getCtors(){
        return ctors;
    }

    public void addCtor(ToolMethod tm) throws AmbiguousMethodDefinitionException {
        getCtors().add(tm);
    }

    public void addCtors(List<ToolMethod> tms)throws  AmbiguousMethodDefinitionException {
        for (ToolMethod tm : tms) {
            addCtor(tm);
        }
    }


    public void addInstanceMethod(ToolMethod tm) throws AmbiguousMethodDefinitionException {
        getInstanceMethods().add(tm);
    }
    public void addInstanceMethods(List<ToolMethod> tms) throws AmbiguousMethodDefinitionException {
        for(ToolMethod tm:tms){
            addInstanceMethod(tm);
        }
    }

    public MethodTable getInstanceMethods() {
        return instanceMethods;
    }

    public void setNameTable(HashMap<String,Memory.NameKind> nameTable) {
        this.nameTable = nameTable;
    }
}
