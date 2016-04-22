package com.parsleyj.utils.reversiblestream;

import com.parsleyj.utils.NoEnoughElementsException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 22/04/16.
 * TODO: javadoc
 */
public class StackedReversibleStream <T> implements ReversibleStream<T> {

    private ArrayDeque<ArrayDeque<T>> streams = new ArrayDeque<>();

    public StackedReversibleStream(List<T> elements){
        streams.add(new ArrayDeque<>(elements));
    }


    @Override
    public void checkPoint() {
        streams.push(new ArrayDeque<>(streams.peek()));
    }

    @Override
    public void rollback() {
        streams.pop();
    }

    @Override
    public void commit() {
        ArrayDeque<T> stream = streams.pop();
        streams.clear();
        streams.push(stream);
    }

    @Override
    public T getNext() throws NoEnoughElementsException {
        return streams.peek().pop();
    }




    @Override
    public List<T> getNext(int howMany) throws NoEnoughElementsException {
        if(streams.peek().size()<howMany) throw new NoEnoughElementsException(streams.peek().size(), howMany);
        List<T> result = new ArrayList<>();
        for(int  i = 0; i < howMany; ++i){
            result.add(streams.peek().pop());
        }
        return result;
    }

    @Override
    public void pushFront(T element) {
        streams.peek().push(element);
    }


    @Override
    public T peek() {
        return streams.peek().peek();
    }

    @Override
    public T peek(int offset) {
        int i = 0;
        for(T t: streams.peek()){
            if(i == offset) return t;
            ++i;
        }
        throw new IndexOutOfBoundsException("Stream size: "+streams.peek().size()+" - searched offset: "+offset);
    }

    @Override
    public boolean isEmpty() {
        return streams.peek().isEmpty();
    }

    @Override
    public int remainingCount() {
        return streams.peek().size();
    }

    @Override
    public ReversibleStream<T> subStreamUntilPredicate(Predicate<T> predicate) {
        if(predicate == null) return new StackedReversibleStream<>(this.toList());
        for(int i = 0; i < remainingCount(); ++i){
            if(predicate.evaluate(this.peek(i))){
                try {
                    return new StackedReversibleStream<>(this.getNext(i));
                } catch (NoEnoughElementsException e) {
                    e.printStackTrace(); //this never happens
                }
            }
        }
        return null;
    }

    @Override
    public List<T> toList() {
        return streams.peek().stream().collect(Collectors.toList());
    }

}
