package com.yihu.ehr.query;

import javafx.util.Pair;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/5/6
 */
public class UpdatePstCallback implements PreparedStatementCallback<Integer> {

    List<Pair<Type, Object>> values;
    public UpdatePstCallback(List<Pair<Type, Object>> values){

        this.values = values;
    }

    @Override
    public Integer doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
        //设参
        setParams(preparedStatement);

        //执行语句
        preparedStatement.executeUpdate();

        //获取id
        int key = getKey(preparedStatement);

        //关闭
        preparedStatement.close();

        return key;
    }

    private int getKey(PreparedStatement preparedStatement) throws SQLException {
        int autoIncKeyFromApi = -1;
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            autoIncKeyFromApi = rs.getInt(1);
        }
        rs.close();
        rs = null;
        return autoIncKeyFromApi;
    }

    public PreparedStatement setParams(PreparedStatement pst) throws SQLException {
        int i=1;
        for(Pair<Type, Object> pair : values){
            pst.setObject(i, pair.getValue());
            i++;
        }
        return pst;
    }

}
