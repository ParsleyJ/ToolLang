package com.parsleyj.tool.memory;

import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.utils.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class Scope {

    public enum ScopeType{Regular, MethodCall, Object}

    private Table<String, Reference> referenceTable = new Table<>();
    private List<PhantomReference> phantomReferences = new ArrayList<>();
    private Integer selfObjectID = null;
    private ScopeType scopeType;

    public Scope(ScopeType scopeType){
        this.scopeType = scopeType;
    }

    public Table<String, Reference> getReferenceTable() {
        return referenceTable;
    }

    public List<PhantomReference> getPhantomReferences() {
        return phantomReferences;
    }

    public boolean contains(String identifier) {
        return referenceTable.contains(identifier);
    }

    public void putReference(Reference r) throws AddedReference {
        referenceTable.put(r.getIdentifierString(), r);
    }


    public Reference newReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        if (contains(identifier)) throw new ReferenceAlreadyExistsException("Reference with name \""+identifier+"\" already exists in this scope.");
        Reference r = new Reference(identifier, o);
        try {
            putReference(r);
            o.increaseReferenceCount();
        } catch (AddedReference addedReference) {
            //exception never thrown, used only as a way to remember to handle reference counting
        }
        return r;
    }

    public Reference getReferenceByName(String identifierString) {
        return referenceTable.get(identifierString);
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public Integer getSelfObjectID() {
        return selfObjectID;
    }

    public void setSelfObjectID(Integer selfObjectID) {
        this.selfObjectID = selfObjectID;
    }
}
