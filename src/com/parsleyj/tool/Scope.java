package com.parsleyj.tool;

import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.utils.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 05/04/16.
 * TODO: javadoc
 */
public class Scope {
    private Table<String, Reference> referenceTable = new Table<>();
    private List<PhantomReference> phantomReferences = new ArrayList<>();

    public Table<String, Reference> getReferenceTable() {
        return referenceTable;
    }

    public List<PhantomReference> getPhantomReferences() {
        return phantomReferences;
    }

    public boolean contains(String identifier) {
        return referenceTable.contains(identifier);
    }

    public void putReference(Reference r) {
        referenceTable.put(r.getIdentifierString(), r);
    }

    public Reference newReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        if (contains(identifier)) throw new ReferenceAlreadyExistsException("Reference with name \""+identifier+"\" already exists in this scope.");
        Reference r = new Reference(identifier, o);
        putReference(r);
        o.increaseReferenceCount();
        return r;
    }

    public Reference getReferenceByName(String identifierString) {
        return referenceTable.get(identifierString);
    }
}
