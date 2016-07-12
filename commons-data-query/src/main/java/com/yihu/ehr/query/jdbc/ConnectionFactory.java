package com.yihu.ehr.query.jdbc;

import com.yihu.ehr.query.common.enums.DBType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.HashMap;


/**
 * 数据库连接工厂类
 * @created hzp on 2015/6/2
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConnectionFactory {

    String defaultName = "defaultName";

    @Value("${spring.datasource.url}")
    String defaultUri;

    @Value("${spring.datasource.username}")
    String defaultUser;

    @Value("${spring.datasource.password}")
    String defaultPassword;

    private final static HashMap<String, ConnectionPool> pools = new HashMap<>();

    /**
     * 使用连接池中连接
     */
    public Connection getConnection(String name) throws Exception
    {
        ConnectionPool source = pools.get(name);
        if(source!=null)
        {
            return source.getConnection();
        }
        else{
            throw new Exception("数据库连接已丢失！");
        }
    }

    /**
     * 使用默认连接
     */
    public Connection getConnection() throws Exception
    {
        ConnectionPool source = pools.get(defaultName);
        if(source!=null)
        {
            return source.getConnection();
        }
        else{
            source = new ConnectionPool(defaultUri, defaultUser, defaultPassword);
            pools.put(defaultName, source);
            return source.getConnection();
        }
    }

    /**
     * 使用默认连接
     */
    public Connection getConnection(String name,String uri) throws Exception
    {
        ConnectionPool source = pools.get(name);
        if(source!=null)
        {
            return source.getConnection();
        }
        else{
            source = new ConnectionPool(uri);
            pools.put(name, source);
            return source.getConnection();
        }
    }
    /**
     * 使用配置连接
     */
    public Connection getConnection(String name,String uri,String user,String password) throws Exception
    {
        ConnectionPool source = pools.get(name);
        if(source!=null)
        {
            return source.getConnection();
        }
        else{
            source = new ConnectionPool(uri, user, password);
            pools.put(name, source);
            return source.getConnection();
        }
    }



    /**
     * 获取数据库类型
     * @return
     */
    public DBType getDbType(){
        return getDbType(defaultUri);
    }

    /**
     * 获取数据库类型
     * @return
     */
    public DBType getDbType(String uri){
        return DBDriver.getDbType(uri);
    }
}
