package com.yihu.ehr.data.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;

/**
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:36
 */
public class ResultUtil {
    private Result result;

    public ResultUtil(Object result){
        this.result = (Result) result;
    }

    public String getCellValue(Object family, Object column, String defaultValue){
        byte[] value = result.getValue(Bytes.toBytes(family.toString()), Bytes.toBytes(column.toString()));

        return value == null ? defaultValue : Bytes.toString(value);
    }

    public List<Cell> listCells(){
        return result.listCells();
    }

    public String getRowKey(){
        return Bytes.toString(result.getRow());
    }

    public String getCellQualifier(Cell cell){
        return Bytes.toString(CellUtil.cloneQualifier(cell));
    }

    public String getCellValue(Cell cell){
        return Bytes.toString(CellUtil.cloneValue(cell));
    }
}
