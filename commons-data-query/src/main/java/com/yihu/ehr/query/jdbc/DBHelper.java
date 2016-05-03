package com.yihu.ehr.query.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.common.enums.DBType;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JDBC数据库操作类
 * Created by hzp on 2015/11/26.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DBHelper {

    @Autowired
    ConnectionFactory factory;
    private String name = "";
    public DBType dbType; //当前连接数据库类型

    /**
     * 获取当前连接
     */
    private Connection getConn() throws Exception
    {
        if (name.length() > 0)
        {
            return factory.getConnection(name);
        }
        else
        {
            return factory.getConnection();
        }
    }

    /**
     * 关闭连接
     */
    private void close(Connection conn) throws Exception{
        if(conn!=null)
        {
            conn.close();
        }
    }

    /**
     * 抛出自定义异常
     * @param msg
     * @param e
     */
    private void rethrow(String msg, Exception e) throws Exception{
        throw new Exception(msg,e);
    }

    private String UNDERLINE = "_";
    /**
     * 驼峰表达式转下划线
     */
    private String camelToUnderline(String val){
        if (val==null||"".equals(val.trim())){
            return "";
        }
        int len=val.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            char c=val.charAt(i);
            if (Character.isUpperCase(c))
            {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            }
            else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Map数据转驼峰表达式
     * @param map
     * @return
     */
    private <T> T MaptoBean( Class<T> cls, Map<String, Object> map) throws Exception{
        Object obj = cls.newInstance();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                String name = property.getName();
                Object val = null;
                if(map.containsKey(name))
                {
                    val = map.get(name);
                }
                else if(map.containsKey(name.toLowerCase()))
                {
                    val = map.get(name.toLowerCase());
                }
                else if(map.containsKey(name.toUpperCase()))
                {
                    val = map.get(name.toUpperCase());
                }
                else {
                    //驼峰命名转下划线
                    String newName = camelToUnderline(name).toLowerCase();
                    if(map.containsKey(newName))
                    {
                        val = map.get(newName);
                    }
                    else if(map.containsKey(newName.toUpperCase()))
                    {
                        val = map.get(newName.toUpperCase());
                    }
                }

                if(val != null) {
                    String type = val.getClass().getName();
                    if (type.indexOf("Date") > 0 || type.indexOf("Timestamp") > 0) //时间格式
                    {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String re = formatter.format(val);
                        val = re;
                    }
                }
                else{
                    val = "";
                }
                setter.invoke(obj, val);
            }
        }
        return (T)obj;

    }
    /*******************************************/
    /**
     * 初始化连接
     */
    public void connect() throws Exception
    {
        factory.getConnection();
        dbType = factory.getDbType();
    }

    /**
     * 自定义连接
     * uri包含用户名/密码
     */
    public void connect(String name,String uri) throws Exception
    {
        this.name = name;
        factory.getConnection(name, uri);
        dbType = factory.getDbType(uri);
    }

    /**
     * 自定义连接
     */
    public void connect(String name,String uri,String user,String password) throws Exception
    {
        this.name = name;
        factory.getConnection(name,uri, user, password);
        dbType = factory.getDbType(uri);
    }

    /****************************** 查询操作 *******************************************/
    /**
     * 查询第一个字段
     */
    public Object scalar(String sql) throws Exception
    {
        return scalar(sql,(Objects[])null);
    }

    /**
     * 查询第一行第一个字段(参数SQL防注入)
     */
    public Object scalar(String sql,Object... params) throws Exception
    {
        Connection conn = getConn();
        try{
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new ScalarHandler(), params);
        }
        catch (Exception e)
        {
            this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), e);
            return null;
        }
        finally {
            this.close(conn);
        }
    }

    /******************** Map查询 *******************************/


    /**
     * 获取单条记录
     */
    public Map<String,Object> load(String sql) throws Exception {
        return load(sql,(Object[])null);
    }

    /**
     * 获取单条记录(参数SQL防注入)
     */
    public Map<String,Object> load(String sql, Object... params) throws Exception {
        Connection conn = getConn();
        try{
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new MapHandler(), params);
        }
        catch (Exception e)
        {
            this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), e);
            return null;
        }
        finally {
            close(conn);
        }
    }

    /**
     * List<JSONObject>获取多条记录
     */
    public List<Map<String,Object>> query(String sql) throws Exception
    {
        return query(sql, (Object[])null);
    }

    /**
     * List<JSONObject>获取多条记录(参数SQL防注入)
     */
    public List<Map<String,Object>> query(String sql,Object... params) throws Exception
    {
        Connection conn = getConn();
        try{
            QueryRunner qr = new QueryRunner();
            return qr.query(conn, sql, new MapListHandler(), params);
        }
        catch (Exception e)
        {
            this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), e);
            return null;
        }
        finally {
            close(conn);
        }
    }

    /*************************** 实体查询(转驼峰) ********************************/
    /**
     * 获取单个实体
     */
    public <T> T load(Class<T> cls, String sql) throws Exception
    {
        return load(cls, sql, (Object[])null);
    }

    /**
     * 获取单个实体
     */
    public <T> T load(Class<T> cls, String sql, Object... params) throws Exception {
        Map<String,Object> map = this.load(sql, params);

        return this.MaptoBean(cls,map);
    }

    /**
     * 获取实体列表
     */
    public <T> List<T> query(Class<T> cls, String sql) throws Exception
    {
        return query(cls, sql, (Object[]) null);
    }

    /**
     * 获取实体列表
     */
    public <T> List<T> query(Class<T> cls, String sql, Object... params) throws Exception {
        List<Map<String,Object>> mapList = this.query(sql, params);
        List<T> list = new ArrayList<>();
        if(mapList!=null && mapList.size()>0)
        {
            for(Map<String,Object> map : mapList)
            {
                list.add(this.MaptoBean(cls,map));
            }
        }

        return list;
    }


    /******************************* 非查询操作 ***********************************/
    /**
     * 测试连接
     */
    public static boolean test(String uri) throws  Exception{
        try {
            java.util.Properties info = new java.util.Properties();
            Connection conn = DriverManager.getDriver(uri).connect(uri,info);
            Boolean re = true;
            if (conn == null) {
                re = false;
            } else {
                if(conn.isClosed())
                {
                    re = false;
                }
                conn.close();
            }
            return re;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * 单条增删改操作
     * @param sql
     * @return
     */
    public boolean execute(String sql) throws Exception{
        return execute(sql,null);
    }

    /**
     * 单条增删改操作
     * @param sql
     * @return
     */
    public boolean execute(String sql,Object... params) throws Exception{
        Connection conn = getConn();
        try{
            QueryRunner qr = new QueryRunner();
            int re = qr.update(conn, sql,params);
            if(re>0)
                return true;
            else {
                this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), null);
                return false;
            }
        }
        catch (Exception e)
        {
            this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), e);
            return false;
        }
        finally {
            close(conn);
        }
    }

    /**
     * 批量操作（同一条SQL语句）
     * @return
     */
    public boolean executeBatch(String sql,Object[][] params) throws Exception
    {
        Connection conn = getConn();
        try{
            QueryRunner qr = new QueryRunner();
            conn.setAutoCommit(false);
            int re = qr.batch(conn, sql, params).length;
            conn.commit();

            if(re>0)
                return true;
            else {
                this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), null);
                return false;
            }
        }
        catch (Exception e)
        {
            if (conn != null) {
                conn.rollback();
            }
            this.rethrow("SQL:" + sql + ",Parameters:" + Arrays.deepToString(params), e);
            return false;
        }
        finally {
            close(conn);
        }
    }

    /**
     * 批量操作(最多1000条执行一次)
     * @param sqls
     * @return
     */
    public boolean executeBatch(String[] sqls) throws Exception
    {
        Connection conn = getConn();
        try{
            conn.setAutoCommit(false);
            Statement statemenet = conn.createStatement();
            final int batchSize = 1000;
            int count = 0;
            for (String sql : sqls) {
                statemenet.addBatch(sql);
                if(++count % batchSize == 0) {
                    statemenet.executeBatch();
                }
            }
            statemenet.executeBatch();
            conn.commit();
            statemenet.close();

            return true;
        }
        catch (Exception e)
        {
            if (conn != null) {
                conn.rollback();
            }
            this.rethrow("SQL:" + Arrays.deepToString(sqls), e);
            return false;
        }
        finally {
            close(conn);
        }
    }

}