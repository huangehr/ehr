package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecord.family.TextFamily;
import com.yihu.ehr.medicalRecord.model.DTO.Text;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
public class TextDao {

    @Autowired
    HBaseUtil hbase;

    /**
     * 新增记录
     */
    public boolean insert(Text obj)throws Exception{
        if(obj.getCreater() !=null && obj.getContent()!=null && obj.getBusinessClass()!=null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String rowkey = TextFamily.getRowkey(obj.getCreater(),obj.getPatientId());
            hbase.insertRecord(TextFamily.TableName, rowkey, TextFamily.Data, obj.getColumns(), obj.getValues());
            return true;
        }
        else
            return false;
    }

    /**
     * 删除记录
     * @return
     */
    public boolean delete(String rowkey) throws Exception
    {
        return true;
    }

    /**
     * 通过rowkey获取数据
     */
    public Map<String, Object> getText(String rowkey)throws Exception{
        return hbase.getResultMap(TextFamily.TableName, rowkey);
    }

    /******************************** solr查询 ********************************************/
    /**
     * 获取素材列表
     */
    public List<Map<String, Object>> getList(String creatorId, String patientId, String businessClass) {
        return null;
    }

    /**
     * 判断是否存在
     */
    public boolean checkExist(String creatorId,String patientId,String businessClass,String content){
        FilterList filterList = new FilterList();
        Filter vf = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("ROW2_QUAL1"));
        Filter vs = new SingleColumnValueFilter(Bytes.toBytes(TextFamily.Data),Bytes.toBytes(TextFamily.DataColumns.BusinessClass),CompareFilter.CompareOp.EQUAL, new SubstringComparator("1"));
        Scan scan = new Scan();
        scan.setFilter(vs);
        ResultScanner scanner = hbase.getScanner(TextFamily.TableName,scan);
        for(Result r:scanner) {
            for (KeyValue kv : r.raw()) {
                System.out.print(new String(kv.getRow()) + " ");
                System.out.print(new String(kv.getFamily()) + ":");
                System.out.print(new String(kv.getQualifier()) + " ");
                System.out.print(kv.getTimestamp() + " ");
                System.out.println(new String(kv.getValue()));
            }
        }
        return true;
    }

}
