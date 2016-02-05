package com.yihu.ehr.util.parm;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
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

    /**
     * 格式化完整的语句， jap形式
     * @return
     */
    public String formatAll(){
        if (modelClass == null) {
            System.err.print("NullPoint: modelClass");
            return "";
        }
        String sql = " select ";
        if (result == null || result.length == 0)
            sql += "tb";
        else {
            List<String> ls = new ArrayList<>();
            for (String item : result) {
                if (!StringUtils.isEmpty(getTableCol(item)))
                    ls.add(item);
            }
            sql += arrayJoin(ls, ",", 1);
        }
        sql += " from "+modelClass.getSimpleName()+" tb ";
        String wh = format("", false);
        if(!StringUtils.isEmpty(wh))
            sql += " where " + wh;

        String order = formatOrder("", false);
        if(!StringUtils.isEmpty(order))
            sql += " order by " + order;
        return sql;
    }

    /**
     * 格式化查询语句， 不包含order by
     * @param modelName  视图名
     * @param isSql true：返回sql形式， false：返回jpa形式
     * @return
     */
    public String format(String modelName, boolean isSql) {
        if (modelClass == null) {
            System.err.print("NullPoint: modelClass");
            return "";
        }
        Map<String, FieldCondition> filters = getFilters();
        if (filters.size() == 0)
            return "";
        Map<String, String> whMap = new HashMap<>();
        FieldCondition fieldCondition;
        String wh = "";
        for (String k : filters.keySet()) {
            fieldCondition = filters.get(k);
            if (!fieldCondition.isValid())
                continue;
            if (fieldCondition.isGroup()) {
                String str = whMap.get(fieldCondition.getGroup());
                if (str == null)
                    str = "(" + fieldCondition.format(modelName, isSql);
                else
                    str += " or " + fieldCondition.format(modelName, isSql);
                whMap.put(fieldCondition.getGroup(), str);
            } else {
                if (wh.equals(""))
                    wh = fieldCondition.format(modelName, isSql);
                else
                    wh += " and " + fieldCondition.format(modelName, isSql);
            }
        }
        for (String k : whMap.keySet()) {
            wh += " and " + whMap.get(k) + ") ";
        }
        return wh;
    }

    /**
     * 格式化查询语句， 不包含order by
     * @return 返回jpa形式语句
     */
    public String format() {
        return format("", false);
    }

    /**
     * 格式化查询语句， 不包含order by
     * @param modelName 视图名
     * @return 返回sql形式语句
     */
    public String formatSql(String modelName) {
        return format(modelName, true);
    }

    /**
     * 格式化查询语句， 不包含order by
     * @return 返回sql形式语句
     */
    public String formatSql() {
        return formatSql("");
    }

    /**
     * 格式化查询语句， 包含order by
     * @param modelName 视图名
     * @return 返回jpa形式语句
     */
    public String formatWithOrder(String modelName) {
        String order = formatOrder(modelName, false);
        if(!order.equals(""))
            return format(modelName, false) + " order by " + order;
        return format(modelName, false);
    }

    /**
     * 格式化查询语句， 包含order by
     * @param modelName 视图名
     * @return 返回sql形式语句
     */
    public String formatSqlWithOrder(String modelName) {
        String order = formatSqlOrder(modelName);
        if(!order.equals(""))
            return formatSql(modelName) + " order by " + order;
        return formatSql(modelName);
    }


    /************************************************************************************/
    /***************            格式化order by 语句的方法                    ************/
    /***************                                                         ************/
    /************************************************************************************/
    /**
     * 格式化oder by，返回sql形式语句
     * @param modelName  视图名
     * @return
     */
    public String formatSqlOrder(String modelName) {
        return formatOrder(modelName, true);
    }

    /**
     * 格式化oder by，返回sql形式语句
     * @return
     */
    public String formatSqlOrder() {
        return formatSqlOrder("");
    }

    /**
     * 格式化oder by，返回jpa形式语句
     * @return
     */
    public String formatOrder() {
        return formatOrder("", false);
    }

    /**
     * 格式化oder by
     * @param modelName 视图名
     * @param isSql true：返回sql形式， false：返回jpa形式
     * @return
     */
    public String formatOrder(String modelName, boolean isSql) {
        if (modelClass == null) {
            System.err.print("NullPoint: modelClass");
            return "";
        }
        if (order == null || order.length == 0)
            return "";
        List<String> ls = new ArrayList<>();
        String tmp = "";
        if (isSql) {
            for (String item : order) {
                tmp = getTableCol(item);
                if (!StringUtils.isEmpty(tmp))
                    ls.add(formatOneOrder(tmp));

            }
        } else
            for (String item : order) {
                tmp = getTableCol(item);
                if (!StringUtils.isEmpty(tmp))
                    ls.add(formatOneOrder(item));
            }
        return arrayJoin(ls, StringUtils.isEmpty(modelName) ? "," : "," + modelName + ".", 1);
    }



    /************************************************************************************/
    /***************            工具                                         ************/
    /***************                                                         ************/
    /************************************************************************************/
    /**
     * 获取过略字段值
     * @param field 名称
     * @return 值
     */
    public Object getFieldVal(String field) {
        return filters.get(field).getVal();
    }

    /**
     * 设置字段值
     * @param field 名称
     * @param val 值
     */
    public void setFieldVal(String field, List val) {
        filters.get(field).setVal(val);
    }

    /**
     * 添加过滤器
     * @param fieldCondition
     */
    public void addFieldCondition(FieldCondition fieldCondition) {
        if (filters == null)
            filters = new HashMap<>();
        filters.put(fieldCondition.getCol(), fieldCondition);
    }

    /**
     * 首字母转换为大写
     * @param str
     * @return
     */
    private static String firstLetterToUpper(String str) {
        if (str == null || "".equals(str.trim())) {
            return "";
        }
        return str.replaceFirst(("" + str.charAt(0)), ("" + str.charAt(0)).toUpperCase());
    }

    /**
     * 确认数据表是否包含有该过滤字段
     * @param field
     * @return
     */
    private String getTableCol(String field) {
        try {
            Method method = modelClass.getMethod("get" + firstLetterToUpper(field));
            Column column = method.getDeclaredAnnotation(Column.class);
            if (column != null) {
                return column.name();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析排序字段
     * @param srcOrder
     * @return
     */
    private String formatOneOrder(String srcOrder){
        if(srcOrder.startsWith("-"))
            return srcOrder.substring(1) +" desc ";
        if(srcOrder.startsWith("+"))
            return srcOrder.substring(1) +" asc ";
        return srcOrder + " asc ";
    }

    /**
     * 数组转换为字符串，
     * @param ls
     * @param joinStr
     * @param offer
     * @return
     */
    private String arrayJoin(Collection<String> ls, String joinStr, int offer) {
        if (ls == null || ls.size() == 0)
            return "";
        String tmp = "";
        for (String str : ls) {
            tmp += joinStr + str;
        }
        return tmp.substring(offer);
    }



    /************************************************************************************/
    /***************            getter  &  setter                            ************/
    /***************                                                         ************/
    /************************************************************************************/

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
        return filters == null ? new HashMap<>() : filters;
    }

    public void setFilters(Map<String, FieldCondition> filters) {
        this.filters = filters;
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
        for (String key : map.keySet()) {
            map.get(key).setTableCol(getTableCol(key));
        }
    }
}
