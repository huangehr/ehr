package com.yihu.ehr.query.common.model;


import java.util.List;

/**
 * Created by hzp on 2015/11/16.
 */
public class DataList {
    private String name;
    private long page = 0;
    private long count = 0;
    private int size = 50;
    private List list;

    public DataList()
    {

    }
    public DataList(long _page,long _count)
    {
        page = _page;
        count = _count;
    }

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
