package com.yihu.ehr.geography.service;
import com.yihu.ehr.model.dict.MConventionalDict;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;

/**
 * Created by Administrator on 2016/2/23.
 */
public class Util {

    public static void main(String[] args) {
        String tableName = "system_dict_entries";
        MConventionalDict dict = new MConventionalDict();
        dict.setCode("2");
        dict.setCode("Approved");
        dict.setValue("审批通过");
        getSQLOfSelect(dict,tableName);
    }

    private static Logger LOG = Logger.getLogger(Logger.class.getName());

    /**
     * 通过Bean对象获取查询语句
     * @param obj 对象
     * @param tableName 表名
     * @return SQL
     */
    public static String getSQLOfSelect(Object obj,String tableName) {
        StringBuffer strBuffer = new StringBuffer("SELECT ") ;
        Class<? extends Object> objClass = obj.getClass();
        Field fields[] = objClass.getDeclaredFields();
        for (Field field:fields) {
            String methodName = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            try {
                if(null != objClass.getMethod("get"+methodName)){
                    strBuffer.append(field.getName()+",") ;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        strBuffer.delete(strBuffer.length()-1, strBuffer.length());
        strBuffer.append(" FROM "+ tableName);
        LOG.info(strBuffer.toString());
        return strBuffer.toString();
    }


    /**
     * 通过bean对象获取插入时的SQL
     * @param obj java对象
     * @param tableName 表名
     * @return SQL
     */
//    public static String getSQLOfInsert(Object obj,String tableName) {
//        String strSQL = new String("INSERT INTO "+ tableName + "(#COLS) VALUES (#VALS)") ;
//        Class<? extends Object> objClass = obj.getClass();
//        Field fields[] = objClass.getDeclaredFields();
//        StringBuffer cols = new StringBuffer("") ;
//        StringBuffer values = new StringBuffer("");
//
//        for (Field field:fields) {
//            String methodName = "get"+ field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
//            try {
//                Method method =  objClass.getMethod(methodName);
//                Object o = method.invoke(obj);
//                if(null != o){
//                    cols.append(field.getName()+",") ;
//                    if(o instanceof Integer || o instanceof Long || o instanceof Double){
//                        values.append(o.toString() + ",");
//                    }else if(o instanceof String){
//                        values.append("'"+ o +"',");
//                    }else if(o instanceof java.util.Date){
//                        String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date)o);
//                        values.append("'"+ datestr +"',");
//                    }else {
//                        values.append("'"+ o.toString() +"',");
//                    }
//                }
//            } catch (SecurityException e) {
//                e.printStackTrace();
//                continue;
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//                continue;
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
//        if(cols.length()>1 && values.length()>1){
//            cols.delete(cols.length()-1, cols.length());
//            values.delete(values.length()-1, values.length());
//            strSQL = strSQL.replace("#COLS", cols).replace("#VALS", values);
//        }else{
//            LOG.warning("警告：空对象无法完成操作啊");
//            return null ;
//        }
//        LOG.info(strSQL);
//
//        return strSQL;
//    }
//
    /**
     * 通过缓存结果集构造对象列表
     * @param clazz javaBean类
     * @param crs   缓存结果集
     * @return 对象列表
     */
    public static List<Object> getBeanFormCacheRowSet(Class<?> clazz, CachedRowSet crs){

        List<Object> result = new ArrayList<Object>();
        try {
            ResultSetMetaData md = crs.getMetaData();
            int columnCount = md.getColumnCount();
            Object o = null;
            while (crs.next()) {
                o = clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    if(crs.getObject(i) == null){
                        continue ;
                    }
                    md.getColumnType(i);
                    String className = md.getColumnClassName(i);
                    if("java.sql.Timestamp".equals(className) || "java.sql.Date".equals(className)){
                        className = "java.util.Date" ;
                    }else if("java.sql.Time".equals(className)){
                        className = "java.lang.String" ;
                    }else if("java.lang.Byte".equals(className) || "java.lang.Short".equals(className)){
                        className = "java.lang.Integer" ;
                    }
                    String methodName = "set"+md.getColumnName(i).substring(0,1).toUpperCase() + md.getColumnName(i).substring(1);
                    try{
                        clazz.getMethod(methodName,Class.forName(className)).invoke(o, crs.getObject(i));
                    }catch(NoSuchMethodException e){
                        LOG.warning("特殊类型字段导致表字段与JavaBean属性不对应！没有找到参数类型是["+className +"]的SETER方法："+ methodName);
                        continue;
                    }
//                  System.out.println("className : " + className +" \tmethodName : " + methodName);
                }
                result.add(o);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
