package com.parsleyj.tool.memory;

import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.tool.exceptions.ReferenceNotFoundException;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.toolparser.configuration.ConfigurationElement;
import com.parsleyj.utils.Table;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Giuseppe on 30/03/16.
 * TODO: javadoc
 */
public class Memory implements ConfigurationElement {

    public static final String SELF_IDENTIFIER = "this";

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
        scopes.add(new Scope(Scope.ScopeType.Regular));
    }

    public void pushStaticMethodCallFrame(){
        scopes.add(new Scope(Scope.ScopeType.MethodCall));
    }

    public void pushInstanceMethodCallFrame(ToolObject selfObject) throws ReferenceAlreadyExistsException {
        Scope scope =new Scope(Scope.ScopeType.MethodCall);
        scopes.add(scope);
        newLocalReference(SELF_IDENTIFIER, selfObject);
    }



    public Scope getTopScope(){
        return scopes.getLast();
    }

    public ToolObject getObjectByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifierString);
        return getObjectById(r.getPointedId());
    }

    public ToolObject getObjectById(Integer id){
        ToolObject to = objectTable.get(id);
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
            result = getObjectByIdentifier(SELF_IDENTIFIER);
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
        ToolObject oldO = getObjectById(r.getPointedId());
        try {
            oldO.decreaseReferenceCount();
        } catch (CounterIsZeroRemoveObject counterIsZeroRemoveObject) {
            removeObject(oldO.getId());
        }
        objectTable.put(o.getId(),o);
        r.setPointedId(o.getId());
        o.increaseReferenceCount();
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
        gcScopeBeforeDisposal(getTopScope());
        this.scopes.removeLast();
    }

    public void removeObject(int id){
        ToolObject o = objectTable.get(id);
        gcScopeBeforeDisposal(o.getMembersScope());
        objectTable.remove(id);
    }

    private void gcScopeBeforeDisposal(Scope scope){
        Table<String, Reference> t = scope.getReferenceTable();
        List<PhantomReference> lpr = scope.getPhantomReferences();
        for (PhantomReference pr : lpr){
            Integer id = pr.getPointedId();
            ToolObject to = objectTable.get(id);
            if(to == null) continue;
            try {
                to.decreaseReferenceCount();
            } catch (CounterIsZeroRemoveObject counterIsZeroRemoveObject) {
                removeObject(id);
            }

        }
        for (String s: t.keySet()) {
            Reference r = t.get(s);
            Integer id = r.getPointedId();
            ToolObject to = objectTable.get(id);
            if(to == null) continue;
            try {
                to.decreaseReferenceCount();
            } catch (CounterIsZeroRemoveObject counterIsZeroRemoveObject) {
                removeObject(id);
            }
        }
    }

    public void loadClasses(List<ToolClass> allBaseClasses) {
        for(ToolClass c:allBaseClasses){
            try {
                this.newLocalReference(c);
            } catch (ReferenceAlreadyExistsException e) {
                e.printStackTrace(); //in theory, these class are the first added when the memory is initialized
            }
        }
    }
}
