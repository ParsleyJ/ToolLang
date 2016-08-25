package com.parsleyj.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 01/05/16.
 * TODO: javadoc
 */
public class Lol {
    public static final boolean DEBUG = true;
    public static final boolean VERBOSE = false;

    public static List<String> flags = new ArrayList<>();

    public static void v(String x){
        if(VERBOSE) System.out.println(x);
    }

    public static void vl(String x){
        if(VERBOSE) System.out.print(x);
    }

    public static void d(String x){
        if(DEBUG) System.out.println(x);
    }

    public static void dl(String x){
        if(DEBUG) System.out.print(x);
    }

    public static void withFlag(String flag, String s) {
        if(flags.contains(flag)) System.err.println(s);
    }

    public static void addFlag(String flag){
        flags.add(flag);
    }
}
