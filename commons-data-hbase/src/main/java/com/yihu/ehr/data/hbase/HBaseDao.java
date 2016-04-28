package com.yihu.ehr.data.hbase;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Bean使用原型模式。
 */
@Service
public class HBaseDao extends AbstractHBaseClient{
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