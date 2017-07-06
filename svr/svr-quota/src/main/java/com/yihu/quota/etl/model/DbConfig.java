package com.yihu.quota.etl.model;

import com.yihu.quota.etl.Contant;
/**
 * Created by chenweida on 2017/6/1.
 */
public class DbConfig {
    private String driver;//数据库驱动
    private String url; //数据库链接
    private String username; //账号
    private String password;//密码
    private String sql;// 语句 如果是多条就是分子
    private String sqlCount;// 语句数目
    private String secondSql;// 语句 分母
    private String secondSqlCount;// 语句数目


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlCount() {
        return sqlCount;
    }

    public void setSqlCount(String sqlCount) {
        this.sqlCount = sqlCount;
    }

    public String getSecondSql() {
        return secondSql;
    }

    public void setSecondSql(String secondSql) {
        this.secondSql = secondSql;
    }

    public String getSecondSqlCount() {
        return secondSqlCount;
    }

    public void setSecondSqlCount(String secondSqlCount) {
        this.secondSqlCount = secondSqlCount;
    }

    public String getDbType() {
        if(driver.contains(Contant.db_type.mysql)){
            return Contant.db_type.mysql;
        }
        if(driver.contains( Contant.db_type.oracle)){
            return  Contant.db_type.oracle;
        }
        return null;
    }
}
