package com.yihu.ehr.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 16:37
 */
public interface XHBaseClient {
    // 表操作
    public boolean isTableExists(String tableName) throws IOException;

    public void createTable(String tableName, String... columnFamilies) throws IOException;

    public void dropTable(String tableName) throws IOException;

    public List<String> getTableList(final String regex, final boolean includeSysTables) throws IOException;

    // 批量记录操作
    public void beginBatchInsert(String tableName, boolean synchronizeMode) throws IOException;

    public void batchInsert(String rowKey, String columnFamily, Object[] columns, Object[] values) throws IOException;

    public void endBatchInsert() throws IOException;

    public Object[] getRecords(String tableName, String[] rowKeys, String[] familyNames);

    public Object[] deleteRecords(String tableName, String[] rowKeys);

    public void truncate(List<String> tables) throws IOException;

    // 单记录操作
    public void insertRecord(String tableName, String rowKey, String columnFamily, Object[] columns, Object[] values) throws IOException;

    public void updateRecord(String tableName, String rowKey, String familyName, String columnName, String value) throws IOException;

    public void deleteRecord(String tableName, String rowKey) throws IOException;

    public void deleteFamily(String tableName, String rowKey, String familyName) throws IOException;

    public void deleteColumn(String tableName, String rowKey, String familyName, String columnName) throws IOException;

    public Result getRecord(String tableName, String rowKey) throws IOException;

    public Result getPartialRecord(String tableName, String rowKey, String[] familyNames, String[][] innerCodes) throws IOException;

    public Result[] getPartialRecords(String tableName, String[] rowKeys, String[] familyNames, String[][] innerCodes);

    public String getValue(String tableName, String rowKey, String familyName, String columnName) throws IOException;

    public ResultWrapper getResultAsWrapper(String tableName, String rowKey) throws IOException;

    //
    public ObjectNode getTableMetaData(String tableName);

    // Scanner
    public ResultScanner getScanner(String tableName, Scan scan);
}
