package com.yihu.ehr.query.common.model;


import java.util.List;

/**
 * Created by hzp on 2015/11/16.
 */
public class DataList {
    private String name;
    private long page = 0;
    private long count = 0;
    private List list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPage(){
        return page;
    }

    public void setPage(long page){
        this.page = page;
    }

    public long getCount(){
        if(count==0 && list !=null && list.size() > 0)
        {
            count = list.size();
        }

        return count;
    }

    public void setCount(long count){
        this.count = count;
    }

    /**
     * 获取列表
     * @return
     */
    public List getList(){
        return list;
    }

    public void setList(List list){
        this.list=list;
    }


}
