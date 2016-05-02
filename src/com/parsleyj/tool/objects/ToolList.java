package com.parsleyj.tool.objects;

import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @NativeInstanceMethod
    public static ToolObject _elementAt_(@SelfParameter ToolList self, ToolList indexes) {
        List<Integer> flattenIndexes = getFlattenIndexList(indexes);

        if (flattenIndexes.size() < 1) throw new InvalidIndexListException();

        if (flattenIndexes.size() == 1) return elementAtWithBackIndexes(self, flattenIndexes.get(0));

        List<ToolObject> resultListContents = flattenIndexes.stream()
                .map(ind -> elementAtWithBackIndexes(self, ind))
                .collect(Collectors.toList());
        return new ToolList(resultListContents);

    }

    private static List<Integer> getFlattenIndexList(ToolList indexes) {
        if (!allIndexesAreIntegersOrLists(indexes)) throw new InvalidIndexTypeException();
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

    private static ToolObject elementAtWithBackIndexes(ToolList list, int index) {
        List<? extends ToolObject> toolObjects = list.getToolObjects();
        if (index >= 0) {
            if (index >= toolObjects.size()) throw new IndexOutOfBoundsExceptionTool();
            return toolObjects.get(index);
        } else {
            if (toolObjects.size() + index < 0) throw new IndexOutOfBoundsExceptionTool();
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

    public static class InvalidIndexTypeException extends RuntimeException { //TODO: tool exception

    }

    public static class InvalidIndexListException extends RuntimeException {
    } //TODO: tool exception

    public static class IndexOutOfBoundsExceptionTool extends RuntimeException { //TODO: tool exception
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
