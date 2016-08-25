package com.parsleyj.tool.semantics.flowcontrol;

import com.parsleyj.tool.exceptions.InvalidBreakExpression;
import com.parsleyj.tool.exceptions.InvalidReturnExpression;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.semantics.base.RValue;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by Giuseppe on 25/08/16.
 * TODO: javadoc
 */
public class ReturnStatement implements RValue {

    public static final String RETURNABLE_CALL_FRAME_TAG = "returnable";

    private final String tag;
    private final RValue resultExpression;

    public ReturnStatement(RValue resultExpression) {
        this(resultExpression, RETURNABLE_CALL_FRAME_TAG);
    }

    public ReturnStatement(RValue resultExpression, String tag){
        this.tag = tag;
        this.resultExpression = resultExpression;
    }

    @Override
    public ToolObject evaluate(Memory memory) throws ToolNativeException {
        ToolObject result = resultExpression.evaluate(memory);
        ArrayDeque<Memory.CallFrame> frames = memory.getFrames();
        Iterator<Memory.CallFrame> it = memory.getFrames().descendingIterator();
        boolean returnWillBeCaptured = false;
        while(it.hasNext()){
            Memory.CallFrame s = it.next();
            if(s.containsTag(tag)) returnWillBeCaptured = true;
        }
        if(!returnWillBeCaptured) throw new InvalidReturnExpression(memory,
                "Return command not corresponding to any call frame. Return tag: "+tag);
        /*while (!frames.isEmpty()&&frames.getLast().containsTag(tag)) {
            frames.removeLast();
        }*/
        if(frames.isEmpty()) throw new InvalidReturnExpression(memory,
                "Return command not captured. Return tag: "+tag);
        throw new Return(tag, result);
    }




    public class Return extends RuntimeException {
        private String tag;
        private ToolObject result;

        public Return(String tag, ToolObject result) {
            super("Return command not captured. Return tag: "+tag);
            this.tag = tag;
            this.result = result;
        }

        public String getTag() {
            return tag;
        }

        public ToolObject getResult() {
            return result;
        }

    }
}
