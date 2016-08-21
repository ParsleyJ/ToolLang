package com.parsleyj.tool.objects.basetypes;

import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.annotations.methods.SelfParameter;
import com.parsleyj.tool.objects.annotations.methods.MemoryParameter;
import com.parsleyj.tool.objects.annotations.methods.NativeInstanceMethod;
import com.parsleyj.tool.objects.method.special.ToolGetterMethod;
import com.parsleyj.tool.objects.method.special.ToolOperatorMethod;
import com.parsleyj.tool.semantics.nametabled.DefinitionGetter;

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

    public ToolIntegerRange(Memory m, Integer start, Integer end) {
        super(m, m.baseTypes().C_INTEGER_RANGE);
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
    public ToolObject iterator(@MemoryParameter Memory memory0) throws ToolNativeException {
        ToolObject result = new ToolObject(memory0);

        result.newMember("index", new ToolInteger(memory0, this.start));

        result.addMethod(DefinitionGetter.createGetter(memory0, "hasNext", memory -> {
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) selfIterator.getReferenceMember("index").getValue();
            return new ToolBoolean(memory,
                    (!this.descending) ? (index.getIntegerValue()<=this.end) : (index.getIntegerValue() >= this.end));
        }));

        result.addMethod(DefinitionGetter.createGetter(memory0, "next", memory -> { //TODO: add index out of bounds check
            ToolObject selfIterator = memory.getSelfObject();
            ToolInteger index = (ToolInteger) selfIterator.getReferenceMember("index").getValue();
            ToolInteger oldIndex = new ToolInteger(memory, index.getIntegerValue());
            index.setIntegerValue((!this.descending) ? index.getIntegerValue()+1 : index.getIntegerValue()-1);
            return oldIndex;
        }));

        return result;
    }

    @NativeInstanceMethod(value = "toList", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolList toList(@MemoryParameter Memory m) throws ToolNativeException {
        List<ToolObject> integers = new ArrayList<>();
        int i = this.start;
        while(this.descending?(i>=this.end):(i<=this.end)){
            integers.add(new ToolInteger(m, i));
            if(this.descending) i--; else i++;
        }
        return new ToolList(m, integers);
    }

    @NativeInstanceMethod(value = "reverse", category = ToolGetterMethod.METHOD_CATEGORY_GETTER)
    public ToolIntegerRange reverse(@MemoryParameter Memory m) throws ToolNativeException{
        return new ToolIntegerRange(m, this.end, this.start);
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
