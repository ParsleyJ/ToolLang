package com.parsleyj.tool;

import com.parsleyj.tool.interpreter.LiteralPattern;
import com.parsleyj.tool.utils.InternalUtils;

import java.util.*;

/**
 * Created by Giuseppe on 17/05/15.
 */
public class TNamespace {

    private TObject owner;

    private Deque<Map<String, TReference>> stack = new ArrayDeque<Map<String, TReference>>();

    private THeapMemory heap;

    public TNamespace(TObject owner, THeapMemory heapMemory) {
        this.owner = owner;
        stack.addFirst(new HashMap<>());
        heap = heapMemory;
    }

    public Integer addObjectToHeap(TObject object) {
        heap.put(object.getId(), object);
        return object.getId();
    }

    public TReference addObjectToScope(String identifier, TObject object) {
        TReference result = new TReference(new TIdentifier(identifier), object.getId()); //TODO: use instantiator defined in TOOL?
        heap.put(object.getId(), object);
        stack.getLast().put(identifier, result);
        return result;
    }

    public TReference addNullReferenceToScope(String identifier){
        TReference result = new TReference(new TIdentifier(identifier), TBaseTypes.NULL_OBJECT.getId());
        stack.getLast().put(identifier, result);
        return result;
    }

    public TReference addClassToScope(TClass tClass) {
        return addObjectToScope(tClass.getClassName(), tClass);
    }


    public TObject getReferencedObject(String identifier) {
        TReference r = getFirstReferenceInScope(identifier);
        if (r == null) return InternalUtils.throwError(TBaseTypes.REFERENCE_NOT_FOUND_ERROR_CLASS,
                        "No <Reference> object found for identifier: '" + identifier + "' " +
                                "in " + owner + "'s memory.");
        return getReferencedObject(r);
    }

    public TObject getReferencedObject(TReference r) {
        TObject result = heap.get(r.getReferencedId());
        if (result == null) result = TBaseTypes.NULL_OBJECT;
        return result;
    }

    public void addToRootScope(TReference reference) {
        stack.getFirst().put(reference.getTIdentifier().getIdentifierString(), reference);
    }

    public void addToCurrentScope(TReference reference) {
        stack.getLast().put(reference.getTIdentifier().getIdentifierString(), reference);
    }

    public int pushNewScope() {
        return pushScope(new HashMap<>(), new HashMap<>());

    }

    public int popScope() {
        stack.removeLast();
        return stack.size();
    }

    public int pushScope(HashMap<String, TReference> memory, HashMap<LiteralPattern, TReference> literalScope) {
        stack.addLast(memory);
        return stack.size();
    }

    public TReference getFirstReferenceInScope(String identifier) {
        Iterator<Map<String, TReference>> i = stack.descendingIterator();
        while (i.hasNext()) {
            TReference ref = i.next().get(identifier);
            if (ref != null) return ref;
        }
        return null;
    }







    //TODO: garbage collector
}
