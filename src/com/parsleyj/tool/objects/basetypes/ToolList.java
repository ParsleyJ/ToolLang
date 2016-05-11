package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.IndexOutOfBoundsExceptionTool;
import com.parsleyj.tool.exceptions.InvalidIndexListException;
import com.parsleyj.tool.exceptions.InvalidIndexTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolList extends ToolObject {
    public List<? extends ToolObject> toolObjects;

    public ToolList(List<? extends ToolObject> list) {
        super(BaseTypes.C_LIST);
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

    public List<? extends ToolObject> getToolObjects() {
        return toolObjects;
    }

    @NativeInstanceMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.MultiEnclosedSuffix)
    public static ToolObject _elementAt_(@ImplicitParameter ToolList self, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolList indexes) throws ToolNativeException {
        List<Integer> flattenIndexes = getFlattenIndexList(indexes);

        if (flattenIndexes.size() < 1) throw new InvalidIndexListException("At least one index is needed.");

        if (flattenIndexes.size() == 1) return elementAtWithBackIndexes(self, flattenIndexes.get(0));

        List<ToolObject> resultListContents = new ArrayList<>();
        for (Integer index : flattenIndexes) {
            resultListContents.add(elementAtWithBackIndexes(self, index));
        }
        return new ToolList(resultListContents);

    }

    private static List<Integer> getFlattenIndexList(ToolList indexes) throws ToolNativeException {
        if (!allIndexesAreIntegersOrLists(indexes)) throw new InvalidIndexTypeException("All index elements have to be Integers (or derivates) or Lists of Integers (or derivates).");
        List<Integer> result = new ArrayList<>();
        for (ToolObject to : indexes.getToolObjects()) {
            if(to.getBelongingClass().isOrExtends(BaseTypes.C_INTEGER)){
                result.add(((ToolInteger) to).getIntegerValue());
            }else if(to.getBelongingClass().isOrExtends(BaseTypes.C_LIST)){
                result.addAll(getFlattenIndexList((ToolList) to));
            }
        }
        return result;
    }

    private static ToolObject elementAtWithBackIndexes(ToolList list, int index) throws ToolNativeException {
        List<? extends ToolObject> toolObjects = list.getToolObjects();
        if (index >= 0) {
            if (index >= toolObjects.size()) throw new IndexOutOfBoundsExceptionTool(index, toolObjects.size());
            return toolObjects.get(index);
        } else {
            if (toolObjects.size() + index < 0) throw new IndexOutOfBoundsExceptionTool(index, toolObjects.size());
            return toolObjects.get(toolObjects.size() + index);
        }
    }

    private static boolean allIndexesAreIntegersOrLists(ToolList indexes) {
        for (ToolObject to : indexes.getToolObjects()) {
            if (!to.getBelongingClass().isOrExtends(BaseTypes.C_LIST) && !to.getBelongingClass().isOrExtends(BaseTypes.C_INTEGER)) {
                return false;
            }
        }
        return true;
    }

    //TODO add(index, obj)
    //TODO addLast(obj)
    //TODO addFirst(obj)
    //TODO addAll(list)
    //TODO remove(index)
    //TODO removeLast()
    //TODO removeFirst()
    //TODO removeAll()
    //TODO getLast()
    //TODO getFirst()
    //TODO get(index)

}
