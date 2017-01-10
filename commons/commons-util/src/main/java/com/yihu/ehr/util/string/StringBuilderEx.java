package com.yihu.ehr.util.string;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.10 13:37
 */
public class StringBuilderEx {
    StringBuilder builder;
    int argIndex = 1;

    public StringBuilderEx(String string){
        this.builder = new StringBuilder(string);
    }

    public StringBuilderEx replace(String src, String dest){
        int index = builder.indexOf(src);
        builder.replace(index, index + src.length(), dest);

        return this;
    }

    public StringBuilderEx arg(String value){
        replace("%" + argIndex++, value);

        return this;
    }

    public StringBuilderEx arg(int value){
        replace("%" + argIndex++, Integer.toString(value));

        return this;
    }

    public StringBuilderEx arg(long value){
        replace("%" + argIndex++, Long.toString(value));

        return this;
    }

    public String toString(){
        return builder.toString();
    }
}
