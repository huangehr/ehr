package com.yihu.ehr.resource.common;

import java.util.List;

/**
 * 字典对象 add by hzp at 20160401
 */
public class DictionaryResult extends Result {

    private String name;
    private List<DictItem> detailModelList;

    public DictionaryResult()
    {}

    public DictionaryResult(String name)
    {
        this.name=name;
    }

    public List<DictItem> getDetailModelList() {
        return detailModelList;
    }

    public void setDetailModelList(List<DictItem> detailModelList) {
        this.detailModelList = detailModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


