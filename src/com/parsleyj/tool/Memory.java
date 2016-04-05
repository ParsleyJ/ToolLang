package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.tool.exceptions.ReferenceNotFoundException;
import com.parsleyj.toolparser.configuration.ConfigurationElement;
import com.parsleyj.utils.Table;

import java.util.*;

/**
 * Created by Giuseppe on 30/03/16.
 * TODO: javadoc
 */
public class Memory implements ConfigurationElement {

    private final String name;
    private ArrayDeque<Scope> scopes = new ArrayDeque<>();
    private Table<Integer, ToolObject> objectTable = new Table<>();

    public Memory(String memoryName){
        this.name = memoryName;
    }

    @Override
    public String getConfigurationElementName() {
        return name;
    }

    public void pushScope(){
        scopes.add(new Scope());
    }

    public Scope getTopScope(){
        return scopes.getLast();
    }

    public ToolObject getObjectByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifierString);
        return getObjectByReference(r);
    }

    public ToolObject getObjectById(Integer id){
        ToolObject to = objectTable.get(id);
        if (to == null) {
            return BaseTypes.O_NULL;
        }
        return to;
    }

    public ToolObject getObjectByReference(Reference ref){ //todo this could lead to error when managing with references not in memory:
                                                           //todo remove this and use only getObjectById?
        ToolObject to = objectTable.get(ref.getPointedId());
        if (to == null) {
            return BaseTypes.O_NULL;
        }
        return to;
    }

    public Reference getReferenceByIdentifier(String identifierString) throws ReferenceNotFoundException{
        Iterator<Scope> i = scopes.descendingIterator();
        while(i.hasNext()){
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            if(t.contains(identifierString)){
                return t.get(identifierString);
            }
        }
        throw new ReferenceNotFoundException("Reference with name: "+identifierString+" not found.");
    }

    public ToolObject getSelfObject() {
        ToolObject result = null;
        try {
            result = getObjectByIdentifier("self");//TODO: change with a defined identifier, or a separate reference system.
        } catch (ReferenceNotFoundException e) {
            e.printStackTrace(); //Throw internal error ToolException
        }
        if (result == null){
            return BaseTypes.O_NULL;
        }else return result;
    }

    public Reference newLocalReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        Reference r = this.getTopScope().newReference(identifier, o);
        this.objectTable.put(o.getId(), o);
        return r;
    }


    public Reference newLocalReference(ToolClass c) throws ReferenceAlreadyExistsException{
        return newLocalReference(c.getClassName(), c);
    }

    public Reference updateReference(String identifier, ToolObject o) throws ReferenceNotFoundException{
        Reference r = getReferenceByIdentifier(identifier);
        return updateReference(r, o);
    }

    public Integer addObjectToHeap(ToolObject o){
        objectTable.put(o.getId(), o);
        return o.getId();
    }

    public Reference updateReference(Reference r, ToolObject o) throws ReferenceNotFoundException{
        ToolObject oldO = getObjectByReference(r);
        oldO.decreaseReferenceCount();
        if(oldO.getReferenceCount() <= 0) objectTable.remove(oldO.getId());
        o.increaseReferenceCount();
        objectTable.put(o.getId(),o);
        r.setPointedId(o.getId());
        return r;
    }

    //adds a phantom reference in the scope below the current one (useful to return values from a scope)
    public void createPhantomReference(ToolObject o){
        Iterator<Scope> i = scopes.descendingIterator();
        i.next();
        Scope p = i.next();
        p.getPhantomReferences().add(new PhantomReference(o));
        o.increaseReferenceCount();
    }

    @Override
    public boolean toBePrinted() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("{\n");
        result.append("\tObjects:").append(objectTable).append("\n");
        Iterator<Scope> i = scopes.descendingIterator();
        result.append("\tScopes:\n");
        while(i.hasNext()){
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            result.append("\t\tscope:").append(t).append("\n");
        }
        result.append("}");
        return result.toString();
    }

    public void popScopeAndGC() {
        Scope p = this.scopes.removeLast();
        Table<String, Reference> t = p.getReferenceTable();
        List<PhantomReference> lpr = p.getPhantomReferences();
        for (PhantomReference pr : lpr){
            Integer id = pr.getPointedId();
            ToolObject to = objectTable.get(id);
            if(to == null) continue;
            to.decreaseReferenceCount();
            if(to.getReferenceCount() <= 0){
                objectTable.remove(id);
            }
        }
        for (String s: t.keySet()) {
            Reference r = t.get(s);
            Integer id = r.getPointedId();
            ToolObject to = objectTable.get(id);
            if(to == null) continue;
            to.decreaseReferenceCount();
            if(to.getReferenceCount() <= 0){
                objectTable.remove(id);
            }
        }
    }

    public void loadClasses(List<ToolClass> allBaseClasses) {
        for(ToolClass c:allBaseClasses){
            try {
                this.newLocalReference(c);
            } catch (ReferenceAlreadyExistsException e) {
                e.printStackTrace(); //theoretically, these class are the first added when the memory is initialized
            }
        }
    }
}
