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

    public enum NameKind{Variable, Accessor, VariableAndAccessor, Method}

    /**
     * Created by Giuseppe on 05/04/16.
     * TODO: javadoc
     */
    public static class Scope {

        public enum ScopeType{Regular, MethodCall, Object, ClassDefinition}

        private ScopeType scopeType;
        private Memory belongingMemory;
        private ToolClass definedClass = null;
        private Table<String, Reference> referenceTable = new Table<>();
        private List<PhantomReference> phantomReferences = new ArrayList<>();
        private HashMap<String, NameKind> nameTable = new HashMap<>();
        private MethodTable localMethods;

        public Scope(Memory mem, ScopeType scopeType){
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

        public List<PhantomReference> getPhantomReferences() {
            return phantomReferences;
        }

        public boolean contains(String identifier) {
            return referenceTable.contains(identifier);
        }

        public HashMap<String, NameKind> getNameTable() {
            return nameTable;
        }

        public void putReference(Reference r) throws AddedReference {
            referenceTable.put(r.getIdentifierString(), r);
        }

        public Reference newReference(String identifier, ToolObject o) throws ReferenceAlreadyExistsException {
            if (contains(identifier)) throw new ReferenceAlreadyExistsException(belongingMemory, "Reference with name \""+identifier+"\" already exists in this scope.");
            Reference r = new Reference(identifier, o);
            try {
                putReference(r);
                o.increaseReferenceCount();
            } catch (AddedReference addedReference) {
                //exception never thrown, used only as a way to remember to handle reference counting
            }
            return r;
        }

        public MethodTable getMethods(){
            return localMethods;
        }

        public void addMethod(ToolMethod tm) throws AmbiguousMethodDefinitionException {
            localMethods.add(tm);
        }

        public Reference getReferenceByName(String identifierString){
            return referenceTable.get(identifierString);
        }

        public void setNameTable(HashMap<String, NameKind> nameT) {
            this.nameTable = nameT;
        }
        public ScopeType getScopeType() {
            return scopeType;
        }

        public ToolClass getDefinedClass(){
            return definedClass;
        }

        public Memory getBelongingMemory() {
            return belongingMemory;
        }

        public void increaseAllLocalCounters(Memory memory){
            for (Reference r: referenceTable.values()) {
                ToolObject o = memory.getObjectById(r.getPointedId());
                o.increaseReferenceCount();
            }
        }

    }


    public static final String SELF_IDENTIFIER = "this";

    public static final String ARG_IDENTIFIER = "arg";
    private final String name;

    private ArrayDeque<CallFrame> callFrames = new ArrayDeque<>();
    private Table<Integer, ToolObject> heap = new Table<>();
    private BaseTypes baseTypes;

    public Memory(String memoryName){
        this.name = memoryName;
    }

    public void init(){
        baseTypes = new BaseTypes();
        baseTypes.init(this);
    }


    @Override
    public String getConfigurationElementName() {
        return name;
    }

    public void pushScope(){
        callFrames.getLast().getStack().add(new Scope(this, Scope.ScopeType.Regular));
    }

    public void pushCallFrame(ToolObject owner) {
        callFrames.add(new CallFrame(this, owner));
    }

    public void pushCallFrame(ToolObject owner, ArrayDeque<Scope> definitionScope) {
        callFrames.add(new CallFrame(this, owner, definitionScope));
    }

    public void pushClassDefinitionScope(ToolClass klass){
        callFrames.getLast().getStack().add(new Scope(this, klass));
    }

    public Scope getTopScope(){
        return callFrames.getLast().getStack().getLast();
    }

    public ToolObject getObjectByIdentifier(String identifierString) throws ReferenceNotFoundException {
        Reference r = getReferenceByIdentifier(identifierString);
        return getObjectById(r.getPointedId());
    }

    public ToolObject getObjectById(Integer id){
        ToolObject to = heap.get(id);
        if (to == null) {
            return baseTypes.O_NULL;
        }
        return to;
    }

    public Reference getReferenceByIdentifier(String identifierString) throws ReferenceNotFoundException{
        Iterator<Scope> i = callFrames.getLast().getStack().descendingIterator();
        while(i.hasNext()){
            Scope p = i.next();
            Table<String, Reference> t = p.getReferenceTable();
            if(t.contains(identifierString)){
                return t.get(identifierString);
            }
        }
        throw new ReferenceNotFoundException(this, "Reference with name: "+identifierString+" not found.");
    }

    public ArrayDeque<Scope> getCurrentFrameStack(){
        return callFrames.getLast().getStack();
    }

    public ToolObject getSelfObject() {
        return getCurrentFrame().getOwner();
    }

    public CallFrame getCurrentFrame(){
        return callFrames.getLast();
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

    public void returnFromCallError(){
        popScopeAndGC();
        callFrames.removeLast();
    }

    public void returnFromCall(ToolObject o){
        Iterator<CallFrame> i = callFrames.descendingIterator();
        i.next();
        CallFrame cf = i.next();
        cf.getStack().getLast().getPhantomReferences().add(new PhantomReference(o));
        o.increaseReferenceCount();
        popScopeAndGC();
        callFrames.removeLast();
    }

    //adds a phantom reference in the scope below the current one (useful to return values from a scope)
    public void createPhantomReference(ToolObject o){
        Iterator<Scope> i = callFrames.getLast().getStack().descendingIterator();
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

    public void popScopeAndGC() {
        gcScopeBeforeDisposal(getTopScope());
        this.callFrames.getLast().getStack().removeLast();
    }

    public Triple<ArrayDeque<Scope>,ToolMethod, ToolObject> resolveFunction(String category, String name, List<ToolClass> argumentsTypes) throws ToolNativeException{
        Iterator<Scope> icf = callFrames.getLast().getStack().descendingIterator();
        while (icf.hasNext()){
            Scope sc = icf.next();
            try {
                ToolMethod tm = sc.getMethods().resolve(null, category, name, argumentsTypes);
                return new Triple<>(tm.getDefinitionScope(), tm, tm.getOwnerObject());
            }catch (MethodNotFoundException mnf){
                //ignore and try in previous scope, during next iteration
            }
        }
        throw new MethodNotFoundException(this, MethodNotFoundException.getDefaultMessage(category, null, name, argumentsTypes));

    }

    public void removeObject(int id){
        ToolObject o = heap.get(id);
        o.onDestroy(this);
        gcScopeBeforeDisposal(o.getMembersScope());
        heap.remove(id);
    }

    public void gcScopeBeforeDisposal(Scope scope){
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

    public boolean privateAccessTo(ToolObject o) throws ReferenceNotFoundException {
        return getSelfObject().getBelongingClass().isExactly(o.getBelongingClass());
    }

    public boolean protectedAccessTo(ToolObject o) throws ReferenceNotFoundException {
        return getSelfObject().getBelongingClass().isOrExtends(o.getBelongingClass());
    }

    public void loadBaseClasses(){
        loadClasses(baseTypes.getAllBaseClasses());
    }

    public void loadClass(ToolClass baseClass) throws ReferenceAlreadyExistsException {
        getTopScope().getNameTable().put(baseClass.getClassName(), NameKind.Variable);
        this.newLocalReference(baseClass);
    }

    public void loadClasses(List<ToolClass> allBaseClasses) {
        for(ToolClass c:allBaseClasses){
            try {
                getTopScope().getNameTable().put(c.getClassName(), NameKind.Variable);
                this.newLocalReference(c);
            } catch (ReferenceAlreadyExistsException e) {
                e.printStackTrace(); //in theory, these class are the first added when the memory is initialized
            }
        }
    }

    public Pair<NameKind, Scope> recursivelyGetNameKind(String name){
        ArrayDeque<Scope> scopes = callFrames.getLast().getStack();
        Iterator<Scope> i = scopes.descendingIterator();
        while(i.hasNext()){
            Scope x = i.next();
            NameKind result = x.getNameTable().get(name);
            if(result != null) return new Pair<>(result, x);
        }
        return null;
    }

    public BaseTypes baseTypes(){
        return baseTypes;
    }


}
