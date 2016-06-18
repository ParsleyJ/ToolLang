package com.parsleyj.tool.objects;

/**
 * Created by Giuseppe on 18/06/16.
 * TODO: javadoc
 */
public class NativeConverter<T> {
    private final NativeConverterMethod<T> method;

    public NativeConverter(NativeConverterMethod<T> method){
        this.method = method;
    }

    public ToolObject convert(Object object){
        return method.convert((T) object);
    }


    @FunctionalInterface
    public static interface NativeConverterMethod<T> {
        public ToolObject convert(T object);
    }

}
