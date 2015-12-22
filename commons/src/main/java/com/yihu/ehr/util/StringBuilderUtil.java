package com.yihu.ehr.util;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.10 13:37
 */
public class StringBuilderUtil {
    StringBuilder builder;
    int argIndex = 1;

    public StringBuilderUtil(String string){
        this.builder = new StringBuilder(string);
    }

    public StringBuilderUtil replace(String src, String dest){
        int index = builder.indexOf(src);
        builder.replace(index, index + src.length(), dest);

        return this;
    }

    public StringBuilderUtil arg(String value){
        replace("%" + argIndex++, value);

        return this;
    }

    public StringBuilderUtil arg(int value){
        replace("%" + argIndex++, Integer.toString(value));

        return this;
    }

    public StringBuilderUtil arg(long value){
        replace("%" + argIndex++, Long.toString(value));

        return this;
    }

    public String toString(){
        return builder.toString();
    }
}
