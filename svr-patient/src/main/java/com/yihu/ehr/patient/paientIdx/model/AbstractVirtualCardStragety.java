package com.yihu.ehr.patient.paientIdx.model;

/**
 * Created by zqb on 2015/6/29.
 */
public abstract class AbstractVirtualCardStragety extends AbstractCardStragety {
    @Override
    public String getTableName(){
        return "'AbstractVirtualCard'";
    }

    @Override
    public  String addColumnList(String inList){
        inList +="";
        return inList;
    }

}
