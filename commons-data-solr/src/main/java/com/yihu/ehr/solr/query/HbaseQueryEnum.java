package com.yihu.ehr.solr.query;

/**
 * Created by hzp on 2015/11/18.
 */
public class HbaseQueryEnum {

    /**
     * ��������ö��
     */
    public enum Operation {
        LIKE, LEFTLIKE, RIGHTLIKE, EQUAL,IN,RANGE;
    }

    /**
     * ��ϵ����ö��
     */
    public enum Logical {
        AND, OR, NOT;
    }
}
