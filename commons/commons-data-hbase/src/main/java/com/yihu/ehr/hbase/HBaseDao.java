package com.yihu.ehr.hbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 数据增删改查
 */
@Service
public class HBaseDao extends AbstractHBaseClient {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     *模糊匹配rowkey
     */
    public String[] findRowKeys(String tableName, String rowkeyRegEx) throws Exception {
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

    /**
     *表总条数
     */
    public Integer count(String tableName) throws Exception {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("basic"));
        scan.setFilter(new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^")));

        List<String> list = new LinkedList<>();
        hbaseTemplate.find(tableName, scan, new RowMapper<Void>() {
            @Override
            public Void mapRow(Result result, int rowNum) throws Exception {
                list.add(Bytes.toString(result.getRow()));

                return null;
            }
        });

        return list.size();
    }

    /**
     * 根据 rowkey获取一条记录
     */
    public String get(String tableName, String rowkey) {
        return hbaseTemplate.get(tableName, rowkey,new RowMapper<String>() {

            public String mapRow(Result result, int rowNum) throws Exception {
                if(!result.isEmpty())
                {
                    List<Cell> ceList = result.listCells();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("rowkey",rowkey);
                    if (ceList != null && ceList.size() > 0) {
                        for (Cell cell : ceList) {
                            //默认不加列族
                            // Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()) +"_"
                            map.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                        }
                    }
                    return objectMapper.writeValueAsString(map);
                }
                else{
                    return "";
                }
            }
        });
    }

    /**
     * 通过表名  key 和 列族 和列 获取一个数据
     */
    public String get(String tableName ,String rowkey, String familyName, String qualifier) {
        return hbaseTemplate.get(tableName, rowkey,familyName,qualifier ,new RowMapper<String>(){
            public String mapRow(Result result, int rowNum) throws Exception {
                List<Cell> ceList =   result.listCells();
                String res = "";
                if(ceList!=null&&ceList.size()>0){
                    for(Cell cell:ceList){
                        res = Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    }
                }
                return res;
            }
        });
    }

    /**
     * 通过rowkey获取某行数据
     */
    public Result getResult(String tableName, String rowKey) throws Exception {
        return hbaseTemplate.get(tableName, rowKey, new RowMapper<Result>() {
            public Result mapRow(Result result, int rowNum) throws Exception {
                return result;
            }
        });
    }

    /**
     * 通过rowkey获取多行数据
     */
    public Result[] getResultList(String tableName, List<String> rowKeys, String basicFl, String dFl) throws Exception {
        return hbaseTemplate.execute(tableName, new TableCallback<Result[]>() {
            public Result[] doInTable(HTableInterface table) throws Throwable {
                List<Get> list = new ArrayList<Get>();
                for (String rowKey : rowKeys) {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    if(!StringUtils.isEmpty(basicFl)) {
                        String [] basicArr = basicFl.split(",");
                        for (String basicStr : basicArr) {
                            get.addColumn(Bytes.toBytes("basic"), Bytes.toBytes(basicStr));
                        }
                    }
                    if(!StringUtils.isEmpty(dFl)) {
                        String [] dArr = dFl.split(",");
                        for (String dStr : dArr) {
                            get.addColumn(Bytes.toBytes("d"), Bytes.toBytes(dStr));
                        }
                    }
                    list.add(get);
                }

                return  table.get(list);
            }
        });
    }

    /**
     * 通过rowkey获取某行数据
     */
    public Map<String, Object> getResultMap(String tableName, String rowKey) throws Exception {
        return hbaseTemplate.get(tableName, rowKey, new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(Result result, int rowNum) throws Exception {
                Map<String, Object> map = null;
                if(result!=null) {
                    List<Cell> ceList = result.listCells();
                    if (ceList != null && ceList.size() > 0) {
                        map = new HashMap<String, Object>();
                        map.put("rowkey", rowKey);
                        for (Cell cell : ceList) {
                            //默认不加列族
                            // Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()) +"_"
                            map.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                        }
                    }
                }
                return map;
            }
        });
    }

    /**
     * 修改某行某列值
     */
    public void put(String tableName ,String rowkey, String familyName, String qualifier,String value) throws Exception {
        hbaseTemplate.execute(tableName, new TableCallback<String>() {

            public String doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(rowkey.getBytes());
                p.add(familyName.getBytes(), qualifier.getBytes(), value.getBytes());
                table.put(p);

                return null;
            }
        });
    }

    /**
     * 新增行
     */
    public void add(String tableName , String rowkey, Map<String,Map<String,String>> family) throws Exception {
        hbaseTemplate.execute(tableName, new TableCallback<String>() {
            public String doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(rowkey.getBytes());
                for(String familyName : family.keySet()) {
                    Map<String,String> map = family.get(familyName);

                    for (String qualifier : map.keySet())
                    {
                        String value = map.get(qualifier);
                        p.add(familyName.getBytes(), qualifier.getBytes(), value.getBytes());
                    }
                }
                table.put(p);
                return null;
            }
        });
    }

    /**
     * 新增数据
     */
    public void add(String tableName, String rowKey, String family, Object[] columns, Object[] values) throws Exception {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            public Object doInTable(HTableInterface htable) throws Throwable {
                Put put = new Put(Bytes.toBytes(rowKey));
                for (int j = 0; j < columns.length; j++) {
                    //为空字段不保存
                    if(values[j]!=null) {
                        String column = String.valueOf(columns[j]);
                        String value = String.valueOf(values[j]);
                        put.addColumn(Bytes.toBytes(family),
                                Bytes.toBytes(column),
                                Bytes.toBytes(value));
                    }
                }

                htable.put(put);
                return null;
            }
        });
    }

    /**
     * 根据 rowkey删除一条记录
     */
    public void delete(String tableName, String rowkey)  {
        hbaseTemplate.execute(tableName, new TableCallback<String>() {
            public String doInTable(HTableInterface table) throws Throwable {
                Delete d = new Delete(rowkey.getBytes());
                table.delete(d);

                return null;
            }
        });
    }

    /**
     * 批量删除数据
     */
    public Object[] deleteBatch(String tableName, String[] rowKeys) throws Exception {
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

    /**
     * 删除列族
     */
    public void deleteFamily(String tableName, String rowKey, String familyName) throws Exception {
        hbaseTemplate.delete(tableName, rowKey, familyName);
    }

    /**
     * 删除某列
     */
    public void deleteColumn(String tableName, String rowKey, String familyName, String columnName) throws Exception {
        hbaseTemplate.delete(tableName, rowKey, familyName, columnName);
    }

    /************************************* Bean使用原型模式 ***************************************************************/
    /**
     * 保存数据 原型模式
     */
    public void save(String tableName, TableBundle tableBundle) throws Exception {
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            public Object doInTable(HTableInterface htable) throws Throwable {
                List<Put> puts = tableBundle.putOperations();
                Object[] results = new Object[puts.size()];
                htable.batch(puts, results);
                return null;
            }
        });
    }

    /**
     * 查询数据 原型模式
     */
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

    /**
     * 删除数据 原型模式
     */
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