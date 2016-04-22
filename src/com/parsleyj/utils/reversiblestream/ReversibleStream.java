package com.parsleyj.utils.reversiblestream;

import com.parsleyj.utils.NoEnoughElementsException;

import java.util.List;

/**
 * Created by Giuseppe on 22/04/16.
 * TODO: javadoc
 */
public interface ReversibleStream<T> {
    void checkPoint();

    void rollback();

    void commit();

    T getNext() throws NoEnoughElementsException;

    List<T> getNext(int howMany) throws NoEnoughElementsException;

    void pushFront(T element);

    T peek();

    T peek(int offset);

    boolean isEmpty();

    int remainingCount();

    ReversibleStream<T> subStreamUntilPredicate(Predicate<T> predicate);


    List<T> toList();

    @FunctionalInterface
    interface Predicate<T>{
        boolean evaluate(T arg1);
    }
}
