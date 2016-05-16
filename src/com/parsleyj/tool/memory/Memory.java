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
    public static final String ARG_IDENTIFIER = "arg";

    private final String name;
    private ArrayDeque<Scope> stack = new ArrayDeque<>(); //TODO: multiple stacks for multithreading
    private Table<Integer, ToolObject> heap = new Table<>();


    public Memory(String memoryName){
        this.name = memoryName;
    }

    @Override
    public String getConfigurationElementName() {
        return name;
    }

    public void pushScope(){
        stack.add(new Scope(Scope.ScopeType.Regular));
    }

    public void pushMethodCallFrame() throws ReferenceAlreadyExistsException {
        stack.add(new Scope(Scope.ScopeType.MethodCall));
    }



    public Scope getTopScope(){
        return stack.getLast();
    }

    public ToolObject getObjectByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifierString);
        return getObjectById(r.getPointedId());
    }

    public ToolObject getObjectById(Integer id){
        ToolObject to = heap.get(id);
        if (to == null) {
            return BaseTypes.O_NULL;
        }
        return to;
    }

    public Reference getReferenceByIdentifier(String identifierString) throws ReferenceNotFoundException{
        Iterator<Scope> i = stack.descendingIterator();
        while(i.hasNext()){
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            if(t.contains(identifierString)){
                return t.get(identifierString);
            }
            if(p.getScopeType() == Scope.ScopeType.MethodCall)
                break;
        }
        throw new ReferenceNotFoundException("Reference with name: "+identifierString+" not found.");
    }

    public ToolObject getSelfObject() throws ReferenceNotFoundException {
        return getObjectByIdentifier(SELF_IDENTIFIER);
    }


    public Reference newLocalReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        Reference r = this.getTopScope().newReference(identifier, o);
        this.heap.put(o.getId(), o);
        return r;
    }

    public Reference newLocalReference(ToolClass c) throws ReferenceAlreadyExistsException{
        return newLocalReference(c.getClassName(), c);
    }

    public Reference updateReference(String identifier, ToolObject o) throws ReferenceNotFoundException{
        Reference r = getReferenceByIdentifier(identifier);
        updateReference(r, o);
        return r;
    }

    public Integer addObjectToHeap(ToolObject o){
        heap.put(o.getId(), o);
        return o.getId();
    }

    public void updateReference(Reference r, ToolObject o) throws ReferenceNotFoundException{
        ToolObject oldO = getObjectById(r.getPointedId());
        try {
            oldO.decreaseReferenceCount();
        } catch (CounterIsZeroRemoveObject c) {
            removeObject(oldO.getId());
        }
        heap.put(o.getId(),o);
        r.setPointedId(o.getId());
        o.increaseReferenceCount();
    }

    //adds a phantom reference in the scope below the current one (useful to return values from a scope)
    public void createPhantomReference(ToolObject o){
        Iterator<Scope> i = stack.descendingIterator();
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
        result.append("\tObjects:").append(heap).append("\n");
        Iterator<Scope> i = stack.descendingIterator();
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
        this.stack.removeLast();
    }

    public void removeObject(int id){
        ToolObject o = heap.get(id);
        gcScopeBeforeDisposal(o.getMembersScope());
        heap.remove(id);
    }

    private void gcScopeBeforeDisposal(Scope scope){
        Table<String, Reference> t = scope.getReferenceTable();
        List<PhantomReference> lpr = scope.getPhantomReferences();
        for (PhantomReference pr : lpr){
            Integer id = pr.getPointedId();
            ToolObject to = heap.get(id);
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
            ToolObject to = heap.get(id);
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
