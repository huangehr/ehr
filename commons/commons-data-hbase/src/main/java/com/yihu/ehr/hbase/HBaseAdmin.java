package com.yihu.ehr.hbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service - HBase DDL
 * @author hzp && Progr1mmer
 * @created 2017.05.03
 * @modifey 2017/11/23
 */
@Service
public class HBaseAdmin extends AbstractHBaseClient {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 判断表是否存在
     * @param tableName
     * @return
     * @throws Exception
     */
    public boolean isTableExists(String tableName) throws Exception {
        Connection connection = null;
        Admin admin = null;
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            return admin.tableExists(TableName.valueOf(tableName));
        }finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }

    }

    /**
     * 新建表
     * @param tableName
     * @param columnFamilies
     * @throws Exception
     */
    public void createTable(String tableName, String... columnFamilies) throws Exception {
        Connection connection = null;
        Admin admin = null;
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
                for (String fc : columnFamilies) {
                    tableDescriptor.addFamily(new HColumnDescriptor(fc));
                }
                admin.createTable(tableDescriptor);
            }
        }finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 模糊匹配表名
     * @param regex 表达式
     * @param includeSysTables 是否包含系统表
     * @return
     * @throws Exception
     */
    public List<String> getTableList(String regex, boolean includeSysTables) throws Exception {
        Connection connection = null;
        Admin admin = null;
        List<String> tables = new ArrayList<>();
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            TableName[] tableNames;
            if (regex == null || regex.length() == 0) {
                tableNames = admin.listTableNames();
            } else {
                tableNames = admin.listTableNames(regex, includeSysTables);
            }
            for (TableName tableName : tableNames) {
                tables.add(tableName.getNameAsString());
            }
            return tables;
        }finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }

    }

    /**
     * 批量清空表数据 （直接删除相关表，再新建）
     * @param tables
     * @throws Exception
     */
    public void cleanTable(List<String> tables) throws Exception {
        Connection connection = null;
        Admin admin = null;
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            for (String tableName : tables) {
                TableName tn = TableName.valueOf(tableName);
                if (admin.tableExists(TableName.valueOf(tableName))) {
                    HTableDescriptor descriptor = admin.getTableDescriptor(tn);
                    admin.disableTable(tn);
                    admin.deleteTable(tn);
                    admin.createTable(descriptor);
                }
                else{
                    System.out.print("not exit table "+tableName+".\r\n");
                }
                /*else{
                    HTableDescriptor descriptor = new HTableDescriptor(tableName);
                    descriptor.addFamily(new HColumnDescriptor("basic"));
                    descriptor.addFamily(new HColumnDescriptor("d"));
                    admin.createTable(descriptor);
                }*/
            }
        } finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 删除表
     * @param tableName
     * @throws Exception
     */
    public void dropTable(String tableName) throws Exception {
        Connection connection = null;
        Admin admin = null;
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        } finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 获取表结构
     * @param tableName
     * @return
     * @throws Exception
     */
    public ObjectNode getTableMetaData(String tableName) throws Exception{
        Connection connection = null;
        Admin admin = null;
        try {
            connection = getConnection();
            admin = connection.getAdmin();
            TableName tn = TableName.valueOf(tableName);
            if (admin.tableExists(tn)) {
                ObjectNode objectNode = objectMapper.createObjectNode();
                HTableDescriptor tableDescriptor = admin.getTableDescriptor(tn);
                HColumnDescriptor[] columnDescriptors = tableDescriptor.getColumnFamilies();
                for (int i = 0; i < columnDescriptors.length; ++i) {
                    HColumnDescriptor columnDescriptor = columnDescriptors[i];
                    objectNode.put(Integer.toString(i), Bytes.toString(columnDescriptor.getName()));
                }
                return objectNode;
            }
            return null;
        }finally {
            if(admin != null) {
                admin.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }

}
