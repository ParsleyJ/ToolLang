package com.parsleyj.tool.memory;

import com.parsleyj.tool.exceptions.*;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolClass;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.method.MethodTable;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.toolparser.configuration.ConfigurationElement;
import com.parsleyj.utils.Pair;
import com.parsleyj.utils.Table;
import com.parsleyj.utils.Triple;

import java.util.*;

/**
 * Created by Giuseppe on 30/03/16.
 * TODO: javadoc
 */
public class Memory implements ConfigurationElement {


    /**
     * Created by Giuseppe on 18/05/16.
     * TODO: javadoc
     */
    public static class CallFrame {

        private static int callFrameIdCounter = 0;
        private int id = callFrameIdCounter++;
        private ArrayDeque<Scope> stack;
        private ToolObject owner;


        public CallFrame(Memory belongingMemory, ToolObject owner) {
            this.owner = owner;
            stack = new ArrayDeque<>();
            stack.add(owner.getMembersScope());
            stack.add(new Scope(belongingMemory, Scope.ScopeType.MethodCall));

        }

        public CallFrame(Memory belongingMemory, ToolObject owner, ArrayDeque<Scope> definitionScope) {
            this.owner = owner;
            stack = new ArrayDeque<>();
            stack.addAll(definitionScope);
            stack.add(new Scope(belongingMemory, Scope.ScopeType.MethodCall));
        }

        public ArrayDeque<Scope> getStack() {
            return stack;
        }

        public ToolObject getOwner() {
            return owner;
        }
    }

    public enum NameKind {Variable, Accessor, VariableAndAccessor, Method}

    /**
     * Created by Giuseppe on 05/04/16.
     * TODO: javadoc
     */
    public static class Scope {

        public enum ScopeType {Regular, MethodCall, Object, ClassDefinition}

        private ScopeType scopeType;
        private Memory belongingMemory;
        private ToolClass definedClass = null;
        private Table<String, Reference> referenceTable = new Table<>();
        private HashMap<String, NameKind> nameTable = new HashMap<>();
        private MethodTable localMethods;

        public Scope(Memory mem, ScopeType scopeType) {
            this.belongingMemory = mem;
            this.scopeType = scopeType;
            localMethods = new MethodTable(mem);
        }

        public Scope(Memory belongingMemory, ToolClass klass) {
            this.belongingMemory = belongingMemory;
            this.scopeType = ScopeType.ClassDefinition;
            this.definedClass = klass;
            localMethods = new MethodTable(belongingMemory);
        }

        public Table<String, Reference> getReferenceTable() {
            return referenceTable;
        }

        public boolean contains(String identifier) {
            return referenceTable.contains(identifier);
        }

        public HashMap<String, NameKind> getNameTable() {
            return nameTable;
        }

        public void putReference(Reference r) throws ReferenceAlreadyExistsException {
            if(contains(r.getIdentifierString()))
                throw new ReferenceAlreadyExistsException(belongingMemory, "Reference with name \"" + r.getIdentifierString() + "\" already exists in this scope.");
            referenceTable.put(r.getIdentifierString(), r);
        }

        public Reference newReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
            Reference r = new Reference(identifier, o);
            putReference(r);
            return r;
        }

        public MethodTable getMethods() {
            return localMethods;
        }

        public void addMethod(ToolMethod tm) throws AmbiguousMethodDefinitionException {
            localMethods.add(tm);
        }

        public Reference getReferenceByName(String identifierString) {
            return referenceTable.get(identifierString);
        }

        public void setNameTable(HashMap<String, NameKind> nameT) {
            this.nameTable = nameT;
        }

        public ScopeType getScopeType() {
            return scopeType;
        }

        public ToolClass getDefinedClass() {
            return definedClass;
        }

        public Memory getBelongingMemory() {
            return belongingMemory;
        }


    }


    public static final String SELF_IDENTIFIER = "this";
    public static final String ARG_IDENTIFIER = "arg";
    private final String memoryName;

    private ArrayDeque<CallFrame> callFrames = new ArrayDeque<>();
    private BaseTypes baseTypes;

    public Memory(String memoryName) {
        this.memoryName = memoryName;
    }

    public void init() {
        baseTypes = new BaseTypes();
        baseTypes.init(this);
    }


    @Override
    public String getConfigurationElementName() {
        return memoryName;
    }

    public void pushScope() {
        callFrames.getLast().getStack().add(new Scope(this, Scope.ScopeType.Regular));
    }

    public void pushCallFrame(ToolObject owner) {
        callFrames.add(new CallFrame(this, owner));
    }

    public void pushCallFrame(ToolObject owner, ArrayDeque<Scope> definitionScope) {
        callFrames.add(new CallFrame(this, owner, definitionScope));
    }

    public void pushClassDefinitionScope(ToolClass klass) {
        callFrames.getLast().getStack().add(new Scope(this, klass));
    }

    public Scope getTopScope() {
        return callFrames.getLast().getStack().getLast();
    }

    public ToolObject getObjectByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifierString);
        return r.getValue();
    }


    public Reference getReferenceByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Iterator<Scope> i = callFrames.getLast().getStack().descendingIterator();
        while (i.hasNext()) {
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            if (t.contains(identifierString)) {
                return t.get(identifierString);
            }
        }
        throw new ReferenceNotFoundException(this, "Reference with name: " + identifierString + " not found.");
    }

    public ArrayDeque<Scope> getCurrentFrameStack() {
        return callFrames.getLast().getStack();
    }

    public ToolObject getSelfObject() {
        return getCurrentFrame().getOwner();
    }

    public CallFrame getCurrentFrame() {
        return callFrames.getLast();
    }

    public Reference newLocalReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
        return this.getTopScope().newReference(identifier, o);
    }

    public Reference newLocalReference(ToolClass c) throws ReferenceAlreadyExistsException {
        return newLocalReference(c.getClassName(), c);
    }

    public Reference updateReference(String identifier, ToolObject o) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifier);
        updateReference(r, o);
        return r;
    }


    public void updateReference(Reference r, ToolObject o) throws ReferenceNotFoundException {
        r.setValue(o);
    }

    public void returnFromCallError() {
        popScope();
        callFrames.removeLast();
    }

    public void returnFromCall() {
        popScope();
        callFrames.removeLast();
    }


    @Override
    public boolean toBePrinted() {
        return true;
    }

    @Override
    public String toString() { //TODO: re-impl
        final StringBuilder result = new StringBuilder("{\n");
        /*Iterator<Scope> i = stack.descendingIterator();
        result.append("\tScopes:\n");
        while(i.hasNext()){
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            result.append("\t\tscope:").append(t).append("\n");
        }
        result.append("}");*/
        return result.toString();
    }

    public void popScope() {
        this.callFrames.getLast().getStack().removeLast();
    }

    public Triple<ArrayDeque<Scope>, ToolMethod, ToolObject> resolveFunction(String category, String name, List<ToolClass> argumentsTypes) throws ToolNativeException {
        Iterator<Scope> icf = callFrames.getLast().getStack().descendingIterator();
        while (icf.hasNext()) {
            Scope sc = icf.next();
            try {
                ToolMethod tm = sc.getMethods().resolve(null, category, name, argumentsTypes);
                return new Triple<>(tm.getDefinitionScope(), tm, tm.getOwnerObject());
            } catch (MethodNotFoundException mnf) {
                //ignore and try in previous scope, during next iteration
            }
        }
        throw new MethodNotFoundException(this, MethodNotFoundException.getDefaultMessage(category, null, name, argumentsTypes));

    }


    public boolean privateAccessTo(ToolObject o) throws ReferenceNotFoundException {
        return getSelfObject().getBelongingClass().isExactly(o.getBelongingClass());
    }

    public boolean protectedAccessTo(ToolObject o) throws ReferenceNotFoundException {
        return getSelfObject().getBelongingClass().isOrExtends(o.getBelongingClass());
    }

    public void loadBaseClasses() {
        loadClasses(baseTypes.getAllBaseClasses());
    }

    public void loadClass(ToolClass baseClass) throws ReferenceAlreadyExistsException {
        getTopScope().getNameTable().put(baseClass.getClassName(), NameKind.Variable);
        this.newLocalReference(baseClass);
    }

    public void loadClasses(List<ToolClass> allBaseClasses) {
        for (ToolClass c : allBaseClasses) {
            try {
                getTopScope().getNameTable().put(c.getClassName(), NameKind.Variable);
                this.newLocalReference(c);
            } catch (ReferenceAlreadyExistsException e) {
                e.printStackTrace(); //in theory, these class are the first added when the memory is initialized
            }
        }
    }

    public Pair<NameKind, Scope> recursivelyGetNameKind(String name) {
        ArrayDeque<Scope> scopes = callFrames.getLast().getStack();
        Iterator<Scope> i = scopes.descendingIterator();
        while (i.hasNext()) {
            Scope x = i.next();
            NameKind result = x.getNameTable().get(name);
            if (result != null) return new Pair<>(result, x);
        }
        return null;
    }

    public BaseTypes baseTypes() {
        return baseTypes;
    }


}
