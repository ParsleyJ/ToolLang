package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.BaseTypes;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.ImplicitParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Giuseppe on 16/05/16.
 * TODO: javadoc
 */
public class ToolIntegerRange extends ToolObject implements Iterable<Integer>{
    private Integer start;
    private Integer end;
    private boolean descending;

    public ToolIntegerRange(Integer start, Integer end) {
        super(BaseTypes.C_INTEGER_RANGE);
        this.start = start;
        this.end = end;
        descending = start>end;
    }

    @Override
    public String getPrintString() {
        return "<"+start+", "+end+">";
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setStart(Integer start) {
        this.start = start;
        descending = start>end;
    }

    public void setEnd(Integer end) {
        this.end = end;
        descending = start>end;
    }

    public boolean isDescending() {
        return descending;
    }

    @NativeInstanceMethod(value = "iterator", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolObject iterator(@MemoryParameter Memory memory0, @ImplicitParameter ToolIntegerRange selfRange) throws ToolNativeException {
        ToolObject result = new ToolObject();

        result.writeObjectMember("index", memory0, new ToolInteger(selfRange.start));

        result.addMethod(new ToolGetterMethod("hasNext", result.getBelongingClass(), memory -> {
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) memory.getObjectById(selfIterator.getReferenceMember("index").getPointedId());
            return new ToolBoolean((!selfRange.descending) ? (index.getIntegerValue()<=selfRange.end) : (index.getIntegerValue()>=selfRange.end));
        }));

        result.addMethod(new ToolGetterMethod("next", result.getBelongingClass(), memory -> { //TODO: add index out of bounds check
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) memory.getObjectById(selfIterator.getReferenceMember("index").getPointedId());
            ToolInteger oldIndex = new ToolInteger(index.getIntegerValue());
            index.setIntegerValue((!selfRange.descending) ? index.getIntegerValue()+1 : index.getIntegerValue()-1);
            return oldIndex;
        }));

        return result;
    }

    @NativeInstanceMethod(value = "toList", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolList toList(@ImplicitParameter ToolIntegerRange self) throws ToolNativeException {
        List<ToolObject> integers = new ArrayList<>();
        int i = self.start;
        while(self.descending?(i>=self.end):(i<=self.end)){
            integers.add(new ToolInteger(i));
            if(self.descending) i--; else i++;
        }
        return new ToolList(integers);
    }

    @NativeInstanceMethod(value = "reverse", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public static ToolIntegerRange reverse(@ImplicitParameter ToolIntegerRange self) throws ToolNativeException{
        return new ToolIntegerRange(self.end, self.start);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntegerRangeIterator(start, end, descending);
    }

    public static class IntegerRangeIterator implements Iterator<Integer>{

        private Integer end;
        private boolean descending;
        private int index;

        public IntegerRangeIterator(Integer start, Integer end, boolean descending) {
            this.end = end;
            this.descending = descending;
            index = start;
        }

        @Override
        public boolean hasNext() {
            return (!descending) ? (index<=end) : (index>=end);
        }

        @Override
        public Integer next() {
            return (descending?index--:index++);
        }
    }
}
