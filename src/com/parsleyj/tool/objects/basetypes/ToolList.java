package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.IndexOutOfBoundsExceptionTool;
import com.parsleyj.tool.exceptions.InvalidIndexListException;
import com.parsleyj.tool.exceptions.InvalidIndexTypeException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.utils.PJ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Giuseppe on 04/04/16.
 * TODO: javadoc
 */
public class ToolList extends ToolObject {
    public List<ToolObject> toolObjects;

    public ToolList(List<ToolObject> list) {
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

    public List<ToolObject> getToolObjects() {
        return toolObjects;
    }

    @NativeInstanceMethod(value = "[]", category = ToolOperatorMethod.METHOD_CATEGORY_OPERATOR, mode = ToolOperatorMethod.Mode.MultiEnclosedSuffix)
    public static ToolObject _elementAt_(@ImplicitParameter ToolList self, @ImplicitParameter(Memory.ARG_IDENTIFIER) ToolList indexes) throws ToolNativeException {
        if (!allIndexesAreIntegersOrRanges(indexes)) throw new InvalidIndexTypeException("All index elements have to be instances of Integer or IntegerRange.");
        if(indexes.toolObjects.isEmpty()) throw new InvalidIndexListException("At least one index is needed.");
        List<ToolObject> resultList = new ArrayList<>();
        for(ToolObject ind: indexes.toolObjects){
            if(ind.getBelongingClass().isOrExtends(BaseTypes.C_INTEGER))
                resultList.add(elementAtWithBackIndexes(self, ((ToolInteger) ind).getIntegerValue()));
            else{
                for(Integer i : (ToolIntegerRange) ind){
                    resultList.add(elementAtWithBackIndexes(self, i));
                }
            }
        }

        if(resultList.size() == 1) return resultList.get(0);
        else return new ToolList(resultList);
    }

    @NativeInstanceMethod(value = "indexes", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolIntegerRange indexes(@ImplicitParameter ToolList selfList)throws ToolNativeException{
        return new ToolIntegerRange(0, selfList.getToolObjects().size()-1);
    }

    @NativeInstanceMethod(value = "size", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolInteger size(@ImplicitParameter ToolList selfList) throws ToolNativeException{
        return new ToolInteger(selfList.getToolObjects().size());
    }


    @NativeInstanceMethod(value = "iterator", category = ToolGetterMethod.METHOD_CATEGORY_GETTER) //TODO: change name after tokenizer upgrade
    public static ToolObject iterator(@MemoryParameter Memory memory0, @ImplicitParameter ToolList selfList) throws ToolNativeException {
        ToolObject result = new ToolObject();

        result.writeObjectMember("index", memory0, new ToolInteger(0));

        result.addMethod(new ToolGetterMethod("hasNext", result.getBelongingClass(), memory -> {
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) memory.getObjectById(selfIterator.getReferenceMember("index").getPointedId());
            return new ToolBoolean(index.getIntegerValue()<selfList.toolObjects.size());
        }));

        result.addMethod(new ToolGetterMethod("next", result.getBelongingClass(), memory -> { //TODO: add index out of bounds check
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) memory.getObjectById(selfIterator.getReferenceMember("index").getPointedId());
            Integer oldIndex = index.getIntegerValue();
            index.setIntegerValue(index.getIntegerValue()+1);
            return selfList.toolObjects.get(oldIndex);
        }));

        return result;
    }

    @NativeInstanceMethod(value = "reverse", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolList reverse(@ImplicitParameter ToolList self){
        return new ToolList(PJ.reverse(self.toolObjects));
    }

    //TODO addAll(collection)
    //TODO removeAt(index)
    //TODO remove(obj)
    //TODO getLast()
    //TODO getFirst()
    //TODO get(index)

    @NativeInstanceMethod(value = "add")
    public static ToolList add(@ImplicitParameter ToolList self, ToolObject argument){
        self.getToolObjects().add(argument);
        return self;
    }

    /*@NativeInstanceMethod(value = "add")
    public static ToolList add(@ImplicitParameter ToolList self, ToolInteger index, ToolObject argument){
        self.getToolObjects().add(index.getIntegerValue(), argument);
        return self;
    }*/

    @NativeInstanceMethod(value = "addLast")
    public static ToolList addLast(@ImplicitParameter ToolList self, ToolObject argument){
        self.getToolObjects().add(argument);
        return self;
    }

    @NativeInstanceMethod(value = "addFirst")
    public static ToolList addFirst(@ImplicitParameter ToolList self, ToolObject argument){
        self.getToolObjects().add(0, argument);
        return self;
    }

    @NativeInstanceMethod(value = "remove")
    public static ToolList remove(@ImplicitParameter ToolList self, ToolInteger index){
        self.getToolObjects().remove(index.getIntegerValue().intValue());
        return self;
    }

    @NativeInstanceMethod(value = "removeLast")
    public static ToolList removeLast(@ImplicitParameter ToolList self){
        self.getToolObjects().remove(self.getToolObjects().size()-1);
        return self;
    }

    @NativeInstanceMethod(value = "removeFirst")
    public static ToolList removeFirst(@ImplicitParameter ToolList self){
        self.getToolObjects().remove(0);
        return self;
    }

    @NativeInstanceMethod(value = "removeAll")
    public static ToolList removeAll(@ImplicitParameter ToolList self){
        self.getToolObjects().clear();
        return self;
    }

    @NativeInstanceMethod(value = "last", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolObject last(@ImplicitParameter ToolList self){
        return self.getToolObjects().get(self.getToolObjects().size()-1);
    }

    @NativeInstanceMethod(value = "first", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolObject first(@ImplicitParameter ToolList self){
        return self.getToolObjects().get(0);
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

    private static boolean allIndexesAreIntegersOrRanges(ToolList indexes){
        for(ToolObject to : indexes.getToolObjects()){
            if(!to.getBelongingClass().isOrExtends(BaseTypes.C_INTEGER_RANGE) && !to.getBelongingClass().isOrExtends(BaseTypes.C_INTEGER)) {
                return false;
            }
        }
        return true;
    }






}
