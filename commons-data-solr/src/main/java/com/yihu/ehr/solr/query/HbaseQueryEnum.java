package com.yihu.ehr.solr.query;

/**
 * Created by hzp on 2015/11/18.
 */
public class HbaseQueryEnum {

    /**
     * 条件类型枚举
     */
    public enum Operation {
        LIKE, LEFTLIKE, RIGHTLIKE, EQUAL,IN,RANGE;
    }

    /**
     * 关系类型枚举
     */
    public enum Logical {
        AND, OR, NOT;
    }
}
