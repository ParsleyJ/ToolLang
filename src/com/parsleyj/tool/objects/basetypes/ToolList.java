package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolIndexOutOfBoundsException;
import com.parsleyj.tool.exceptions.InvalidIndexListException;
import com.parsleyj.tool.exceptions.InvalidIndexTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolTuple;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.nametabled.DefinitionGetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolList extends ToolObject {
    public List<ToolObject> toolObjects;

    public ToolList(Memory m, List<ToolObject> list) {
        super(m, m.baseTypes().C_LIST);
        toolObjects = list;
    }

    @Override
    public String getPrintString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < toolObjects.size(); i++) {
            sb.append(toolObjects.get(i));
            if (i < toolObjects.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    public List<ToolObject> getToolObjects() {
        return toolObjects;
    }

    @NativeInstanceMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public ToolObject _elementAt_(@MemoryParameter Memory m, ToolObject index) throws ToolNativeException {
        if (!allIndexesAreIntegersOrRanges(m, new ToolTuple(m, Collections.singletonList(index))))
            throw new InvalidIndexTypeException(m, "All index elements have to be instances of Integer or IntegerRange.");
        List<ToolObject> resultList = new ArrayList<>();
        if (index.getBelongingClass().isOrExtends(m.baseTypes().C_INTEGER))
            resultList.add(elementAtWithBackIndexes(m, this, ((ToolInteger) index).getIntegerValue()));
        else {
            for (Integer i : (ToolIntegerRange) index) {
                resultList.add(elementAtWithBackIndexes(m, this, i));
            }
        }
        if (resultList.size() == 1) return resultList.get(0);
        else return new ToolTuple(m, resultList);
    }

    @NativeInstanceMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR,
            mode = ToolOperatorMethod.Mode.BinaryParametric)
    public ToolObject _elementAt_(@MemoryParameter Memory m, ToolTuple indexes) throws ToolNativeException {
        if (!allIndexesAreIntegersOrRanges(m, indexes)) throw new InvalidIndexTypeException(m,
                "All index elements have to be instances of Integer or IntegerRange.");
        if (indexes.getTupleObjects().isEmpty()) throw new InvalidIndexListException(m, "At least one index is needed.");
        List<ToolObject> resultList = new ArrayList<>();
        for (ToolObject ind : indexes.getTupleObjects()) {
            if (ind.getBelongingClass().isOrExtends(m.baseTypes().C_INTEGER))
                resultList.add(elementAtWithBackIndexes(m, this, ((ToolInteger) ind).getIntegerValue()));
            else {
                for (Integer i : (ToolIntegerRange) ind) {
                    resultList.add(elementAtWithBackIndexes(m, this, i));
                }
            }
        }
        if (resultList.size() == 1) return resultList.get(0);
        else return new ToolTuple(m, resultList);
    }

    @NativeInstanceMethod(value = "indexes", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolIntegerRange indexes(@MemoryParameter Memory m) throws ToolNativeException {
        return new ToolIntegerRange(m, 0, this.getToolObjects().size() - 1);
    }

    @NativeInstanceMethod(value = "size", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolInteger size(@MemoryParameter Memory m) throws ToolNativeException {
        return new ToolInteger(m, this.getToolObjects().size());
    }


    @NativeInstanceMethod(value = "iterator", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolObject iterator(@MemoryParameter Memory memory0) throws ToolNativeException {
        ToolObject result = new ToolObject(memory0);

        result.newMember("index", new ToolInteger(memory0, 0));

        result.addMethod(DefinitionGetter.createGetter(memory0, "hasNext", memory -> {
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) selfIterator.getReferenceMember("index").getValue();
            return new ToolBoolean(memory, index.getIntegerValue() < this.toolObjects.size());
        }));

        result.addMethod(DefinitionGetter.createGetter(memory0, "next", memory -> { //TODO: add index out of bounds check
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) selfIterator.getReferenceMember("index").getValue();
            Integer oldIndex = index.getIntegerValue();
            index.setIntegerValue(index.getIntegerValue() + 1);
            return this.toolObjects.get(oldIndex);
        }));

        return result;
    }

    @NativeInstanceMethod(value = "reverse", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolList reverse(@MemoryParameter Memory m) {
        List<ToolObject> newL = new ArrayList<>();
        this.toolObjects.forEach(to -> newL.add(0, to));
        return new ToolList(m, newL);
    }

    //TODO addAll(collection)
    //TODO removeAt(index)
    //TODO remove(obj)
    //TODO getLast()
    //TODO getFirst()
    //TODO get(index)

    @NativeInstanceMethod(value = "add")
    public ToolList add(@MemoryParameter Memory memory0, ToolObject argument) {
        this.getToolObjects().add(argument);
        return this;
    }

    @NativeInstanceMethod(value = "add")
    public ToolList add(@MemoryParameter Memory memory0, ToolInteger index, ToolObject argument) {
        this.getToolObjects().add(index.getIntegerValue(), argument);
        return this;
    }

    @NativeInstanceMethod(value = "addLast")
    public ToolList addLast(@MemoryParameter Memory memory0, ToolObject argument) {
        this.getToolObjects().add(argument);
        return this;
    }

    @NativeInstanceMethod(value = "addFirst")
    public ToolList addFirst(@MemoryParameter Memory memory0, ToolObject argument) {
        this.getToolObjects().add(0, argument);
        return this;
    }

    @NativeInstanceMethod(value = "remove")
    public ToolList remove(@MemoryParameter Memory memory0, ToolInteger index) {
        this.getToolObjects().remove(index.getIntegerValue().intValue());
        return this;
    }

    @NativeInstanceMethod(value = "removeLast")
    public ToolList removeLast(@MemoryParameter Memory memory0) {
        this.getToolObjects().remove(this.getToolObjects().size() - 1);
        return this;
    }

    @NativeInstanceMethod(value = "removeFirst")
    public ToolList removeFirst(@MemoryParameter Memory memory0) {
        this.getToolObjects().remove(0);
        return this;
    }

    @NativeInstanceMethod(value = "removeAll")
    public ToolList removeAll(@MemoryParameter Memory memory0) {
        this.getToolObjects().clear();
        return this;
    }

    @NativeInstanceMethod(value = "last", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolObject last(@MemoryParameter Memory memory0) {
        return this.getToolObjects().get(this.getToolObjects().size() - 1);
    }

    @NativeInstanceMethod(value = "first", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolObject first(@MemoryParameter Memory memory0) {
        return this.getToolObjects().get(0);
    }

    private static ToolObject elementAtWithBackIndexes(Memory m, ToolList list, int index) throws ToolNativeException {
        List<? extends ToolObject> toolObjects = list.getToolObjects();
        if (index >= 0) {
            if (index >= toolObjects.size()) throw new ToolIndexOutOfBoundsException(m, index, toolObjects.size());
            return toolObjects.get(index);
        } else {
            if (toolObjects.size() + index < 0) throw new ToolIndexOutOfBoundsException(m, index, toolObjects.size());
            return toolObjects.get(toolObjects.size() + index);
        }
    }

    private static boolean allIndexesAreIntegersOrRanges(Memory m, ToolTuple indexes) {
        for (ToolObject to : indexes.getTupleObjects()) {
            if (!to.getBelongingClass().isOrExtends(m.baseTypes().C_INTEGER_RANGE) &&
                    !to.getBelongingClass().isOrExtends(m.baseTypes().C_INTEGER)) {
                return false;
            }
        }
        return true;
    }


}
