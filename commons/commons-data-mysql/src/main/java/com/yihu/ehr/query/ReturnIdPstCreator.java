package com.yihu.ehr.query;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/5/6
 */
public class ReturnIdPstCreator implements PreparedStatementCreator {

    String sql;

    public ReturnIdPstCreator(String sql){
        this.sql = sql;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

}
