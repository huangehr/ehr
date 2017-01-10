package com.yihu.ehr.query.jdbc;

import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;

/**
 *  JDBC数据库连接池
 * Created by hzp on 2016-04-29.
 */
public class ConnectionPool {

    private DriverManagerConnectionFactory connectionFactory; //数据库连接工厂
    private PoolableConnectionFactory poolableConnectionFactory; //可池化数据库连接
    private GenericObjectPool<PoolableConnection> connectionPool;


    public ConnectionPool(String uri) {
        DBDriver.registerDriver(uri);
        connectionFactory = new DriverManagerConnectionFactory(uri, null);
        poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        connectionPool.setMaxIdle(20);
        connectionPool.setMaxTotal(30);
        poolableConnectionFactory.setPool(connectionPool);
    }

    /**
     * 账号密码登录
     * @param uri
     * @param user
     * @param password
     */
    public ConnectionPool(String uri,String user,String password) {
        DBDriver.registerDriver(uri);
        connectionFactory = new DriverManagerConnectionFactory(uri, user,password);
        poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
    }

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConnection() throws Exception{
        return new PoolingDataSource<>(connectionPool).getConnection();
    }



}
