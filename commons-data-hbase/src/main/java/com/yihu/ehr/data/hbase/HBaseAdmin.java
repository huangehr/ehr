package com.yihu.ehr.data.hbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @created 2016.04.27 16:29
 */
@Service
public class HBaseAdmin extends AbstractHBaseClient {
    public boolean isTableExists(String tableName) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        boolean ex = admin.tableExists(TableName.valueOf(tableName));

        admin.close();
        connection.close();

        return ex;
    }

    public void createTable(String tableName, String... columnFamilies) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        if (!admin.tableExists(TableName.valueOf(tableName))) {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            for (String fc : columnFamilies) {
                tableDescriptor.addFamily(new HColumnDescriptor(fc));
            }

            admin.createTable(tableDescriptor);
        }

        admin.close();
        connection.close();
    }

    public List<String> getTableList(String regex, boolean includeSysTables) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        TableName[] tableNames;
        if (regex == null || regex.length() == 0) {
            tableNames = admin.listTableNames();
        } else {
            tableNames = admin.listTableNames(regex, includeSysTables);
        }

        List<String> tables = new ArrayList<>();
        for (TableName tableName : tableNames) {
            tables.add(tableName.getNameAsString());
        }

        admin.close();
        connection.close();

        return tables;
    }

    public void truncate(List<String> tables) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        try {
            for (String tableName : tables) {
                TableName tn = TableName.valueOf(tableName);
                if (admin.tableExists(TableName.valueOf(tableName))) {
                    HTableDescriptor descriptor = admin.getTableDescriptor(tn);
                    admin.disableTable(tn);
                    admin.deleteTable(tn);
                    admin.createTable(descriptor);
                }
            }
        } finally {
            admin.close();
            connection.close();
        }
    }

    public ObjectNode getTableMetaData(String tableName) {
        return hbaseTemplate.execute(tableName, new TableCallback<ObjectNode>() {

            public ObjectNode doInTable(HTableInterface table) throws Throwable {
                ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
                ObjectNode root = objectMapper.createObjectNode();

                HTableDescriptor tableDescriptor = table.getTableDescriptor();
                HColumnDescriptor[] columnDescriptors = tableDescriptor.getColumnFamilies();
                for (int i = 0; i < columnDescriptors.length; ++i) {
                    HColumnDescriptor columnDescriptor = columnDescriptors[i];
                    root.put(Integer.toString(i), Bytes.toString(columnDescriptor.getName()));
                }

                return root;
            }
        });
    }

    private Connection getConnection() throws IOException {
        Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
        return connection;
    }
}
