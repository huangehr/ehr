package com.yihu.ehr.data.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CollectionUtils;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Bean使用原型模式。
 */
@Service
public class HBaseDao extends AbstractHBaseClient {
    public void saveOrUpdate(String tableName, TableBundle tableBundle) throws IOException {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {

            public Object doInTable(HTableInterface htable) throws Throwable {
                List<Put> puts = tableBundle.putOperations();

                Object[] results = new Object[puts.size()];
                htable.batch(puts, results);

                return null;
            }
        });
    }

    public Object[] get(String tableName, TableBundle tableBundle) {
        return hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {

            public Object[] doInTable(HTableInterface table) throws Throwable {
                List<Get> gets = tableBundle.getOperations();

                Object[] results = new Object[gets.size()];
                table.batch(gets, results);

                if (results.length > 0 && results[0].toString().equals("keyvalues=NONE")) return null;

                return results;
            }
        });
    }

    public String[] findRowKeys(String tableName, String rowkeyRegEx) throws Throwable {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("basic"));
        scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(rowkeyRegEx)));

        List<String> list = new LinkedList<>();
        hbaseTemplate.find(tableName, scan, new RowMapper<Void>() {
            @Override
            public Void mapRow(Result result, int rowNum) throws Exception {
                list.add(Bytes.toString(result.getRow()));

                return null;
            }
        });

        return list.toArray(new String[list.size()]);
    }

    public void delete(String tableName, TableBundle tableBundle) {
        hbaseTemplate.execute(tableName, new TableCallback<Object[]>() {

            public Object[] doInTable(HTableInterface table) throws Throwable {
                List<Delete> deletes = tableBundle.deleteOperations();

                Object[] results = new Object[deletes.size()];
                table.batch(deletes, results);

                return null;
            }
        });
    }
}