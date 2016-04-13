package com.yihu.ehr.data.hadoop;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 10:36
 */
public class ResultWrapper {
    private Result result;

    public ResultWrapper(Result result){
        this.result = result;
    }

    public Result getResult(){
        return this.result;
    }

    /**
     *
     * @param family
     * @param qualifier
     * @return
     */
    public String getValueAsString(String family, String qualifier){
        byte[] value = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return Bytes.toString(value);
    }
}
