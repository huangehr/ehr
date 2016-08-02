package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by hzp on 2016/8/2.
 */
@Service
public abstract class HbaseDao {

    @Autowired
    HBaseUtil hbase;


    protected String getTableName(){
        return "";
    }

    /**
     * 删除记录
     * @return
     */
    public boolean delete(String rowkey) throws Exception
    {
        hbase.deleteRecord(getTableName(),rowkey);
        return true;
    }

    /**
     * 通过rowkey获取数据
     */
    public Map<String, Object> getDataByRowkey(String rowkey)throws Exception{
        return hbase.getResultMap(getTableName(), rowkey);
    }


}
