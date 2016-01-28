package com.yihu.ehr.util.ParmUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/28.
 */
public class ParmModel {
    private int page;
    private int rows;
    private Map<String, FieldCondition> filters;


    public String format(String tableName){
        Map<String, FieldCondition> filters = getFilters();
        if(filters.size()==0)
            return "";
        Map<String, String> whMap = new HashMap<>();
        FieldCondition fieldCondition;
        String wh = "";
        for(String k: filters.keySet()){
            fieldCondition = filters.get(k);
            if(fieldCondition.isGroup()){
                String str = whMap.get(fieldCondition.getGroup());
                if(str==null)
                    str = "("+fieldCondition.format(tableName);
                else
                    str += " or " + fieldCondition.format(tableName);
                whMap.put(fieldCondition.getGroup(), str);
            }
            else {
                if(wh.equals(""))
                    wh = fieldCondition.format(tableName);
                else
                    wh += " and "+ fieldCondition.format(tableName);
            }
        }
        for (String k : whMap.keySet()){
            wh += " and " + whMap.get(k) + ") ";
        }
        return wh;
    }

    public String format(){
        return format("");
    }

    public Object getFieldVal(String field){
        return filters.get(field).getVal();
    }

    public void setFieldVal(String field, List val){
        filters.get(field).setVal(val);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Map<String, FieldCondition> getFilters() {
        return filters==null? new HashMap<>() : filters;
    }

    public void setFilters(Map<String, FieldCondition> filters) {
        this.filters = filters;
    }

    public static void main(String[] args){

    }
}
