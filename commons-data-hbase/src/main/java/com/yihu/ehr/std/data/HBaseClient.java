package com.yihu.ehr.std.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.lang.ServiceFactory;
import com.yihu.ehr.util.log.LogService;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
 * HBaseClient。目前使用原型模式，但对在多线程中插入数据与查询表是否存在同时进行产生的等待问题，依然不是解决方案。
 */
@Service(Services.HBaseClient)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HBaseClient implements XHBaseClient {
    @Autowired
    HbaseTemplate hbaseTemplate;

    @Autowired
    SolrClientFactory solrClientFactory;

    String batchTableName;
    boolean synchronizeMode;
    Map<String, Put> batchPuts = new HashMap<>();

    @Override
    public boolean isTableExists(String tableName) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        boolean ex = admin.tableExists(TableName.valueOf(tableName));

        admin.close();
        connection.close();

        return ex;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void insertRecord(String tableName, String rowKey, String columnFamily, Object[] columns, Object[] values) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface htable) throws Throwable {
                Put put = new Put(Bytes.toBytes(rowKey));
                for (int j = 0; j < columns.length; j++) {
                    put.addColumn(Bytes.toBytes(columnFamily),
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

    public void batchInsert(String rowKey, String columnFamily, Object[] columns, Object[] values) throws IOException {
        Put put = batchPuts.get(rowKey);
        if (put == null) {
            put = new Put(Bytes.toBytes(rowKey));
            batchPuts.put(rowKey, put);
        }

        for (int i = 0; i < columns.length; ++i) {
            put.addColumn(Bytes.toBytes(columnFamily),
                    Bytes.toBytes(String.valueOf(columns[i])),
                    Bytes.toBytes(String.valueOf(values[i])));
        }
    }

    public void endBatchInsert() throws IOException {
        hbaseTemplate.execute(batchTableName, new TableCallback<Object>() {
            @Override
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

    @Override
    public Object[] getRecords(String tableName, String[] rowKeys, String[] familyNames) {
        return hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {
            @Override
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

    @Override
    public Object[] deleteRecords(String tableName, String[] rowKeys) {
        return hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {
            @Override
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

    @Override
    public void updateRecord(String tableName, String rowKey, String familyName, String columnName, String value) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface table) throws Throwable {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName), Bytes.toBytes(value));
                table.put(put);

                return null;
            }
        });
    }

    @Override
    public void deleteRecord(String tableName, String rowKey) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @Override
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

    @Override
    public void deleteColumn(String tableName, String rowKey, String familyName, String columnName) throws IOException {
        hbaseTemplate.delete(tableName, rowKey, familyName, columnName);
    }

    @Override
    public Result getRecord(String tableName, String rowKey) throws IOException {
        Result context = hbaseTemplate.get(tableName, rowKey, new RowMapper<Result>() {
            @Override
            public Result mapRow(Result result, int rowNum) throws Exception {
                return result;
            }
        });

        return context;
    }

    @Override
    public Result getPartialRecord(String tableName, String rowKey, String[] familyNames, String[][] innerCodes) throws IOException {
        assert familyNames.length > 0 && innerCodes.length == familyNames.length;

        return hbaseTemplate.execute(tableName, new TableCallback<Result>() {
            @Override
            public Result doInTable(HTableInterface htable) throws Throwable {
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

                Result result = htable.get(get);
                return result;
            }
        });
    }

    @Override
    public Result[] getPartialRecords(String tableName, String[] rowKeys, String[] familyNames, String[][] innerCodes) {
        return hbaseTemplate.execute(tableName, new TableCallback<Result[]>() {
            @Override
            public Result[] doInTable(HTableInterface table) throws Throwable {
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
        });
    }

    @Override
    public String getValue(String tableName, String rowKey, String familyName, String columnName) throws IOException {
        String value = hbaseTemplate.get(tableName, rowKey, familyName, columnName, new RowMapper<String>() {
            @Override
            public String mapRow(Result result, int rowNum) throws Exception {
                return Bytes.toString(result.value());
            }
        });

        return value;
    }

    @Override
    public List<ObjectNode> find(String tableName, String queryString) {
        return find(tableName, queryString, null, null, 100, 1);
    }

    @Override
    public List<ObjectNode> find(String tableName, String queryString, List<String> fields, Map<String, String> sort, int countPerPage, int pageCount) {
        return hbaseTemplate.execute(tableName, new TableCallback<List<ObjectNode>>() {
            @Override
            public List<ObjectNode> doInTable(HTableInterface table) throws Throwable {
                List<Get> list = new ArrayList<>();
                List<ObjectNode> data = new ArrayList<>();

                SolrQuery query = new SolrQuery();
                query.setQuery(queryString);

                int start = 0;
                int pageSize = countPerPage < 0 ? 1 : (countPerPage > 100 ? 100 : countPerPage);

                int page = pageCount < 0 ? 1 : pageCount;
                start = (page - 1) * pageSize;

                query.setStart(start);
                query.setRows(pageSize);

                //设置排序条件
                if (sort != null) {
                    for (String field : sort.keySet()) {
                        SolrQuery.ORDER order = SolrQuery.ORDER.asc;
                        if (sort.get(field) != null && sort.get(field).trim().toLowerCase().equals("desc")) {
                            order = SolrQuery.ORDER.desc;
                        }
                        query.addSort(field, order);
                    }
                }

                QueryResponse rsp;
                CloudSolrClient cloudSolrClient = null;
                try {
                    cloudSolrClient = solrClientFactory.getCloudSolrClient(tableName);
                    rsp = cloudSolrClient.query(query);
                    SolrDocumentList rs = rsp.getResults();

                    // 获取文档的rowKey列表
                    for (SolrDocument doc : rs) {
                        Get get = new Get(Bytes.toBytes(String.valueOf(doc.getFieldValue("rowkey"))));
                        list.add(get);
                    }

                    for (Get gt : list) {
                        Result result = table.get(gt);
                        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                        ObjectNode record = objectMapper.createObjectNode();
                        record.put("rowkey", Bytes.toString(result.getRow()));

                        for (Cell cell : result.listCells()) {
                            String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
                            String fieldValue = Bytes.toString(CellUtil.cloneValue(cell));
                            if (fields != null && fields.size() > 0) {
                                if (fields.contains(fieldName)) {
                                    record.put(fieldName, fieldValue);
                                }
                            } else {
                                record.put(fieldName, fieldValue);
                            }
                        }

                        data.add(record);
                    }
                } catch (Exception e) {
                    LogService.getLogger(HBaseClient.class).error(e.getMessage());
                } finally {
                    if (cloudSolrClient != null) cloudSolrClient.close();
                }

                return data;
            }
        });
    }

    @Override
    public ResultWrapper getResultAsWrapper(String tableName, String rowKey) throws IOException {
        return new ResultWrapper(getRecord(tableName, rowKey));
    }

    @Override
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

    @Override
    public ResultScanner getScanner(String tableName, Scan scan) {
        return hbaseTemplate.execute(tableName, new TableCallback<ResultScanner>() {
            @Override
            public ResultScanner doInTable(HTableInterface hTableInterface) throws Throwable {
                ResultScanner resultScanner = hTableInterface.getScanner(scan);

                return resultScanner;
            }
        });
    }

    @Override
    public List<String> findRowKey(String tableName, String queryString, Map<String, String> sort, int pageSize, int page) throws IOException {
        List<String> list = new ArrayList<>();

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);

        int start = 0;
        if (pageSize < 0) pageSize = 1;
        if (pageSize > 100) pageSize = 100;

        if (page < 0) page = 1;
        start = (page - 1) * pageSize;

        query.setStart(start);
        query.setRows(pageSize);
        query.setFields("rowkey");

        //设置排序条件
        if (sort != null) {
            for (String field : sort.keySet()) {
                SolrQuery.ORDER order = SolrQuery.ORDER.asc;
                if (sort.get(field) != null && sort.get(field).trim().toLowerCase().equals("desc")) {
                    order = SolrQuery.ORDER.desc;
                }
                query.addSort(field, order);
            }
        }

        CloudSolrClient cloudSolrClient = solrClientFactory.getCloudSolrClient(tableName);
        try {
            QueryResponse rsp = cloudSolrClient.query(query);
            SolrDocumentList rs = rsp.getResults();

            for (SolrDocument doc : rs) {
                String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
                list.add(rowkey);
            }
        } catch (Exception e) {
            LogService.getLogger(HBaseClient.class).error(e.getMessage());
        } finally {
            if (cloudSolrClient != null) cloudSolrClient.close();
        }

        return list;
    }

    @Override
    public void clearSolrData(List<String> tables) throws IOException, SolrServerException {
        for (String tableName : tables) {
            CloudSolrClient solr = solrClientFactory.getCloudSolrClient(tableName);
            solr.deleteByQuery("*:*");
            solr.close();
        }
    }

    private Connection getConnection() throws IOException {
        Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
        return connection;
    }

    public ObjectNode getTableMetaData(String tableName) {
        return hbaseTemplate.execute(tableName, new TableCallback<ObjectNode>() {
            @Override
            public ObjectNode doInTable(HTableInterface table) throws Throwable {
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
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
}