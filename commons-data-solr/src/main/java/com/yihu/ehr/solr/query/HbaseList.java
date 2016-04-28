package com.yihu.ehr.solr.query;

import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by hzp on 2015/11/16.
 */
public class HbaseList {
    private String name;
    private long page = 0;
    private long count = 0;
    private List<JSONObject> list;

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
    public List<JSONObject> getList(){
        return list;
    }

    public void setList(List<JSONObject> list){
        this.list=list;
    }

    /**
     * 获取json字符串
     * @return
     */
    public String getJson(){
        if(list!=null&&list.size()>0)
        {
            return "{\"page\":"+page+",\"count\":"+count+",\"data\":"+list.toString()+"}";
        }
        else
            return "{\"page\":"+page+",\"count\":"+count+",\"data\":[]}";
    }
}
