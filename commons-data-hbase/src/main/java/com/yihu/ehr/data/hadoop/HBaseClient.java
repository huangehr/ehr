package com.yihu.ehr.data.hadoop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean使用原型模式。
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HBaseClient {
    @Autowired
    HbaseTemplate hbaseTemplate;

    String batchTableName;
    boolean synchronizeMode;
    Map<String, Put> batchPuts = new HashMap<>();

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

    public void dropTable(String tableName) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        try {
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        } finally {
            admin.close();
            connection.close();
        }
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

    public void insertRecord(String tableName, String rowKey, String family, Object[] columns, Object[] values) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {

            public Object doInTable(HTableInterface htable) throws Throwable {
                Put put = new Put(Bytes.toBytes(rowKey));
                for (int j = 0; j < columns.length; j++) {
                    put.addColumn(Bytes.toBytes(family),
                            Bytes.toBytes(String.valueOf(columns[j])),
                            Bytes.toBytes(String.valueOf(values[j])));
                }

                htable.put(put);

                return null;
            }
        });
    }

    public void beginBatchInsert(String tableName, boolean synchronizeMode) throws IOException {
        this.batchTableName = tableName;
        this.synchronizeMode = synchronizeMode;
        this.batchPuts.clear();
    }

    public void batchInsert(String rowKey, String family, Object[] columns, Object[] values) throws IOException {
        Put put = batchPuts.get(rowKey);
        if (put == null) {
            put = new Put(Bytes.toBytes(rowKey));
            batchPuts.put(rowKey, put);
        }

        for (int i = 0; i < columns.length; ++i) {
            put.addColumn(Bytes.toBytes(family),
                    Bytes.toBytes(String.valueOf(columns[i])),
                    Bytes.toBytes(String.valueOf(values[i])));
        }
    }

    public void endBatchInsert() throws IOException {
        hbaseTemplate.execute(batchTableName, new TableCallback<Object>() {

            public Object doInTable(HTableInterface table) throws Throwable {
                Object[] results = null;

                List<Put> puts = new ArrayList<>(batchPuts.values());
                if (synchronizeMode) {
                    table.put(puts);
                } else {
                    table.batch(puts, results);
                }

                batchTableName = "";
                batchPuts.clear();

                return null;
            }
        });
    }

    public Object[] getRecords(String tableName, String[] rowKeys, String[] familyNames) {
        return hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {

            public Object[] doInTable(HTableInterface table) throws Throwable {
                List<Get> gets = new ArrayList<>(rowKeys.length);
                for (String rowKey : rowKeys) {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    for (String familyName : familyNames) {
                        get.addFamily(Bytes.toBytes(familyName));
                    }

                    gets.add(get);
                }

                Object[] results = new Object[gets.size()];
                table.batch(gets, results);

                return results;
            }
        });
    }

    public Object[] deleteRecords(String tableName, String[] rowKeys) {
        return hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {

            public Object[] doInTable(HTableInterface table) throws Throwable {
                List<Delete> deletes = new ArrayList<>(rowKeys.length);
                for (String rowKey : rowKeys) {
                    Delete delete = new Delete(Bytes.toBytes(rowKey));
                    deletes.add(delete);
                }

                Object[] results = new Object[deletes.size()];
                table.batch(deletes, results);

                return results;
            }
        });
    }

    public void updateRecord(String tableName, String rowKey, String familyName, String columnName, String value) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {

            public Object doInTable(HTableInterface table) throws Throwable {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName), Bytes.toBytes(value));
                table.put(put);

                return null;
            }
        });
    }

    public void deleteRecord(String tableName, String rowKey) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {

            public Object doInTable(HTableInterface table) throws Throwable {
                Delete deleteAll = new Delete(Bytes.toBytes(rowKey));

                table.delete(deleteAll);
                return null;
            }
        });
    }

    public void deleteFamily(String tableName, String rowKey, String familyName) throws IOException {
        hbaseTemplate.delete(tableName, rowKey, familyName);
    }

    public void deleteColumn(String tableName, String rowKey, String familyName, String columnName) throws IOException {
        hbaseTemplate.delete(tableName, rowKey, familyName, columnName);
    }

    public Result getRecord(String tableName, String rowKey) throws IOException {
        Result context = hbaseTemplate.get(tableName, rowKey, new RowMapper<Result>() {

            public Result mapRow(Result result, int rowNum) throws Exception {
                return result;
            }
        });

        return context;
    }

    /**
     * 读取一条记录的部分数据。
     *
     * @param tableName
     * @param rowKey
     * @param familyNames
     * @param innerCodes
     * @return
     * @throws IOException
     */
    public Result getPartialRecord(String tableName, String rowKey, String[] familyNames, String[][] innerCodes) throws IOException {
        assert familyNames.length > 0 && innerCodes.length == familyNames.length;

        return hbaseTemplate.execute(tableName, new TableCallback<Result>() {
            public Result doInTable(HTableInterface table) throws Throwable {
                Result[] results = getRecords(table, new String[]{rowKey}, familyNames, innerCodes);
                return results[0];
            }
        });
    }

    /**
     * 读取多条记录的部分数据。
     *
     * @param tableName
     * @param rowKeys
     * @param familyNames
     * @param innerCodes
     * @return
     */
    public Result[] getPartialRecords(String tableName, String[] rowKeys, String[] familyNames, String[][] innerCodes) {
        return hbaseTemplate.execute(tableName, new TableCallback<Result[]>() {

            public Result[] doInTable(HTableInterface table) throws Throwable {
                return getRecords(table, rowKeys, familyNames, innerCodes);
            }
        });
    }

    private Result[] getRecords(HTableInterface table,
                                String[] rowKeys,
                                String[] familyNames,
                                String[][] innerCodes) throws IOException, InterruptedException {
        List<Get> gets = new ArrayList<>(rowKeys.length);
        for (String rowKey : rowKeys) {
            Get get = new Get(Bytes.toBytes(rowKey));
            for (int i = 0; i < familyNames.length; ++i) {
                String familyName = familyNames[i];
                String[] columns = innerCodes[i];

                if (columns == null || columns.length == 0) {
                    get.addFamily(Bytes.toBytes(familyName));
                    continue;
                } else {
                    for (int j = 0; j < columns.length; ++j) {
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columns[j]));
                    }
                }
            }

            gets.add(get);
        }

        Result[] results = new Result[gets.size()];
        table.batch(gets, results);

        return results;
    }

    public String getCell(String tableName, String rowKey, String familyName, String columnName) throws IOException {
        String value = hbaseTemplate.get(tableName, rowKey, familyName, columnName, new RowMapper<String>() {

            public String mapRow(Result result, int rowNum) throws Exception {
                return Bytes.toString(result.value());
            }
        });

        return value;
    }


    public ResultWrapper getResultAsWrapper(String tableName, String rowKey) throws IOException {
        return new ResultWrapper(getRecord(tableName, rowKey));
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

    public ResultScanner getScanner(String tableName, Scan scan) {
        return hbaseTemplate.execute(tableName, new TableCallback<ResultScanner>() {

            public ResultScanner doInTable(HTableInterface hTableInterface) throws Throwable {
                ResultScanner resultScanner = hTableInterface.getScanner(scan);

                return resultScanner;
            }
        });
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