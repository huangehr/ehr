package com.yihu.ehr.hbase;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.IOException;

/**
 * @author hzp
 * @created 2017.05.03
 */
public class AbstractHBaseClient {
    @Autowired
    protected HbaseTemplate hbaseTemplate;


    /**
     * 创建连接
     */
    protected Connection getConnection() throws Exception {
        return getConnection(hbaseTemplate);
    }


    /**
     * 创建连接
     */
    protected Connection getConnection(HbaseTemplate hbaseTemplate) throws Exception {
        Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
        return connection;
    }
}
