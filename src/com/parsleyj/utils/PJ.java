package com.parsleyj.utils;

import java.util.*;

/**
 * Created by Giuseppe on 18/04/16.
 * TODO: javadoc
 */
public class PJ{

    @SafeVarargs
    public static <T> List<T> list(T... elements){
        return Arrays.asList(elements);
    }

    public static <T> List<T> reverse(List<T> l){
        List<T> result = new ArrayList<>(l);
        Collections.reverse(result);
        return result;
    }

    public static <T> void indexedForEach(Collection<T> c, TwoArgAction<T, Integer> action){
        int index = 0;
        for(T o: c){
            action.doAction(o, index++);
        }
    }


    @FunctionalInterface
    public interface Action{
        void doAction();
    }

    @FunctionalInterface
    public interface OneArgAction<T>{
        void doAction(T o);
    }

    @FunctionalInterface
    public interface TwoArgAction<T1, T2>{
        void doAction(T1 o1, T2 o2);
    }
}
