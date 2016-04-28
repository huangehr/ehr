package com.yihu.ehr.solr.query;

import java.util.HashMap;
import java.util.Map;

/**
 * �Զ����������
 * Created by hzp on 2015/11/17.
 */
public class HbaseQueryGroup {

    private String groupField; //�����ֶ���
    private Map<String,String> groupCondition; //��������

    /**
     * ���캯��
     */
    public HbaseQueryGroup(String groupField)
    {
        this.groupField = groupField;
        this.groupCondition= new HashMap<String,String>();
    }

    /**
     * ���캯��
     */
    public HbaseQueryGroup(String groupField, Map<String, String> groupCondition)
    {
        this.groupField = groupField;
        this.groupCondition= groupCondition;
    }

    /**
     * ��ȡ�����ֶ���
     * @return
     */
    public String getGroupField() {
        return groupField;
    }

    /**
     * ��ȡ��������
     * @return
     */
    public Map<String, String> getGroupCondition() {
        return groupCondition;
    }

    /**
     * ��ӷ���������
     */
    public void putGroupCondition(String key,String condition){
        this.groupCondition.put(key,condition);
    }
}