package com.parsleyj.tool.memory;

import com.parsleyj.tool.exceptions.ReferenceAlreadyExistsException;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.MethodTable;
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
    private Table<String, Reference> methodCallParameters = new Table<>();
    private Table<String, Reference> implicitParameters = new Table<>();
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

    public void putParameter(Reference r) throws AddedReference {
        methodCallParameters.put(r.getIdentifierString(), r);
    }

    public Reference newParameterReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        if (containsParameter(identifier)) throw new ReferenceAlreadyExistsException("Parameter with name \""+identifier+"\" already exists in this scope.");
        Reference r = new Reference(identifier, o);
        try {
            putParameter(r);
            o.increaseReferenceCount();
        } catch (AddedReference addedReference) {
            //exception never thrown, used only as a way to remember to handle reference counting
        }
        return r;
    }

    public boolean containsParameter(String identifier){
        return methodCallParameters.contains(identifier);
    }

    public void putImplicitParameter(Reference r) throws AddedReference {
        implicitParameters.put(r.getIdentifierString(), r);
    }

    public Reference newImplicitParameterReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        if (containsImplicitParameter(identifier)) throw new ReferenceAlreadyExistsException("Implicit parameter with name \""+identifier+"\" already exists in this scope.");
        Reference r = new Reference(identifier, o);
        try {
            putImplicitParameter(r);
            o.increaseReferenceCount();
        } catch (AddedReference addedReference) {
            //exception never thrown, used only as a way to remember to handle reference counting
        }
        return r;
    }

    public boolean containsImplicitParameter(String identifier){
        return implicitParameters.contains(identifier);
    }


    public Table<String, Reference> getMethodCallParameters() {
        return methodCallParameters;
    }

    public Table<String, Reference> getImplicitParameters() {
        return implicitParameters;
    }

    public Reference getParameterByName(String identifierString){
        return methodCallParameters.get(identifierString);
    }

    public Reference getImplicitParameterByName(String identifierString){
        return methodCallParameters.get(identifierString);
    }
}
