package com.yihu.ehr.util.parm;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Administrator on 2016/1/28.
 */
public class PageModel {
    private int page;
    private int rows;
    private String[] order;
    private Map<String, FieldCondition> filters;
    private String[] result;
    private Class modelClass;
    public PageModel() {

    }

    public PageModel(int page, int rows) {
        this.page = page;
        this.rows = rows;
    }

    public String format(String modelName, boolean isSql) {
        if(modelClass==null){
            System.err.print("NullPoint: modelClass");
            return "";
        }
        Map<String, FieldCondition> filters = getFilters();
        if(filters.size()==0)
            return "";
        Map<String, String> whMap = new HashMap<>();
        FieldCondition fieldCondition;
        String wh = "";
        for(String k: filters.keySet()){
            fieldCondition = filters.get(k);
            if(!fieldCondition.isValid())
                continue;
            if(fieldCondition.isGroup()){
                String str = whMap.get(fieldCondition.getGroup());
                if(str==null)
                    str = "("+fieldCondition.format(modelName, isSql);
                else
                    str += " or " + fieldCondition.format(modelName, isSql);
                whMap.put(fieldCondition.getGroup(), str);
            }
            else {
                if(wh.equals(""))
                    wh = fieldCondition.format(modelName, isSql);
                else
                    wh += " and "+ fieldCondition.format(modelName, isSql);
            }
        }
        for (String k : whMap.keySet()){
            wh += " and " + whMap.get(k) + ") ";
        }
        return wh;
    }

    private String getTableCol(String field){
        try {
            Method method = modelClass.getMethod("get" + firstLetterToUpper(field));
            Column column = method.getDeclaredAnnotation(Column.class);
            if (column!=null){
                return column.name();
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public String formatSqlOrder(String modelName) {
        return formatOrder(modelName, true);
    }

    public String formatSqlOrder() {
        return formatSqlOrder("");
    }

    public String formatOrder() {
        return formatOrder("", false);
    }

    public String formatOrder(String modelName, boolean isSql){
        if(modelClass==null){
            System.err.print("NullPoint: modelClass");
            return "";
        }
        if(order==null || order.length==0)
            return "";
        List<String> ls = new ArrayList<>();
        String tmp = "";
        if(isSql){
            for(String item : order){
                tmp = getTableCol(item);
                if(!StringUtils.isEmpty(tmp))
                    ls.add(tmp);
            }
        }
        else
            for(String item : order){
                tmp = getTableCol(item);
                if(!StringUtils.isEmpty(tmp))
                    ls.add(item);
            }
        return arrayJoin(ls, StringUtils.isEmpty(modelName)? "," : ","+modelName+".", 1);
    }

    public String arrayJoin(Collection<String> ls, String joinStr, int offer){
        if(ls==null || ls.size()==0)
            return "";
        String tmp = "";
        for (String str : ls){
            tmp += joinStr + str;
        }
        return tmp.substring(offer);
    }

    public String formatWithOrder(String modelName){
        return  format(modelName, false) + " order by " + formatOrder(modelName, false);
    }
    public String formatSqlWithOrder(String modelName){
        return  formatSql(modelName) + " order by " + formatSqlOrder(modelName);
    }
    public String format(){
        return format("", false);
    }
    public String formatSql(String modelName){
        return format(modelName, true);
    }
    public String formatSql(){
        return formatSql("");
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

    public void addFieldCondition(FieldCondition fieldCondition){
        if(filters==null)
            filters = new HashMap<>();
        filters.put(fieldCondition.getCol(), fieldCondition);
    }

    public String[] getOrder() {
        return order;
    }

    public void setOrder(String[] order) {
        this.order = order;
    }

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public Class getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class modelClass) {
        this.modelClass = modelClass;
        Map<String, FieldCondition> map = getFilters();
        for(String key : map.keySet()){
            map.get(key).setTableCol(getTableCol(key));
        }
//        List<String> result = new ArrayList<>();
//        String tableCol = "";
//        for (String key : getResult()){
//            tableCol = getTableCol(key);
//            if(!StringUtils.isEmpty(tableCol))
//                result.add(tableCol);
//        }
//        setResult((String[]) result.toArray());
//
//        result.clear();
//        for (String key : getOrder()){
//            tableCol = getTableCol(key);
//            if(!StringUtils.isEmpty(tableCol))
//                result.add(tableCol);
//        }
//        setOrder((String[]) result.toArray());
    }

    public static String firstLetterToUpper(String str){
        if(str==null || "".equals(str.trim())){
            return "";
        }
        return str.replaceFirst((""+str.charAt(0)), (""+str.charAt(0)).toUpperCase());
    }

    public static void main(String[] args){

    }

}
