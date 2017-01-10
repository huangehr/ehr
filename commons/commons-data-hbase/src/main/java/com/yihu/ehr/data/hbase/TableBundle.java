package com.yihu.ehr.data.hbase;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 将HBase中的行，列族，列捆绑成一束。并一次性生成所需要的Get, Put操作。
 * <p>
 * 仅支持单表操作。
 * <p>
 * 虽然支持多种HBase操作，但请注意，一次只能用于一种操作，如：Get，Put，Delete不能混用，
 * 否则将出现难以预料的后果。
 *
 * @author Sand
 * @created 2016.04.27 14:38
 */
public class TableBundle {
    Map<String, Row> rows = new HashMap<>();

    public void addRows(String... rowkeys) {
        for (String rowkey : rowkeys) {
            rows.put(rowkey, null);
        }
    }

    public void addFamily(String rowkey, Object family) {
        Row row = getRow(rowkey);
        row.addFamily(family.toString());
    }

    public void addColumns(String rowkey, Object family, String[] columns) {
        Row row = getRow(rowkey);
        row.addColumns(family.toString(), columns);
    }

    public void addValues(String rowkey, Object family, Map<String, String> values) {
        Row row = getRow(rowkey);
        row.addValues(family.toString(), values);
    }

    public void clear() {
        rows.clear();
    }

    public List<Get> getOperations() {
        List<Get> gets = new ArrayList<>(rows.size());
        for (String rowkey : rows.keySet()) {
            Get get = new Get(Bytes.toBytes(rowkey));

            Row row = rows.get(rowkey);
            if (row != null) {
                for (String family : row.getFamilies()) {
                    Set<Object> columns = row.getCells(family);

                    if (CollectionUtils.isEmpty(columns)) {
                        get.addFamily(Bytes.toBytes(family));
                    }

                    for (Object column : columns) {
                        get.addColumn(Bytes.toBytes(family), Bytes.toBytes((String) column));
                    }
                }
            }

            gets.add(get);
        }

        return gets;
    }

    public List<Put> putOperations() {
        List<Put> puts = new ArrayList<>(rows.values().size());
        for (String rowkey : rows.keySet()) {
            Put put = new Put(Bytes.toBytes(rowkey));

            Row row = rows.get(rowkey);
            for (String family : row.getFamilies()) {
                Set<Object> columns = row.getCells(family);

                for (Object column : columns) {
                    Pair<String, String> pair = (Pair<String, String>) column;
                    if (StringUtils.isNotEmpty(pair.getRight())) {
                        put.addColumn(Bytes.toBytes(family),
                                Bytes.toBytes(pair.getLeft()),
                                Bytes.toBytes(pair.getRight()));
                    }
                }
            }

            puts.add(put);
        }

        return puts;
    }

    public List<Delete> deleteOperations() {
        List<Delete> deletes = new ArrayList<>(rows.values().size());
        for (String rowkey : rows.keySet()) {
            Delete delete = new Delete(Bytes.toBytes(rowkey));

            deletes.add(delete);
        }

        return deletes;
    }

    private Row getRow(String rowkey) {
        Row row = rows.get(rowkey);
        if (row == null) {
            row = new Row();
            rows.put(rowkey, row);
        }

        return row;
    }

    /**
     * HBase中的一行
     */
    public static class Row {
        private Map<String, Set<Object>> cells = new HashMap<>();   // key为family，value为columns

        public void addFamily(String family) {
            cells.put(family, null);
        }

        public void addColumns(String family, String... columns) {
            Set value = getFamily(family);
            for (String column : columns) {
                value.add(column);
            }
        }

        public void addValues(String family, Map<String, String> values) {
            Set value = getFamily(family);
            value.addAll(values.keySet().stream().map(key -> new ImmutablePair<>(key, values.get(key))).collect(Collectors.toList()));
        }

        public Set<String> getFamilies() {
            return cells.keySet();
        }

        public Set<Object> getCells(String family) {
            return cells.get(family);
        }

        private Set<Object> getFamily(String family) {
            Set value = cells.get(family);
            if (value == null) {
                value = new TreeSet<>();
                cells.put(family, value);
            }

            return value;
        }
    }
}
