package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecord.family.DocumentFamily;
import com.yihu.ehr.medicalRecord.model.DTO.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
public class DocumentDao {

    @Autowired
    HBaseUtil hbase;

    /**
     * 新增记录
     */
    public boolean insert(Document obj)throws Exception{

        if(obj.getCreater()!=null  && obj.getFileType()!=null && obj.getFileUrl()!=null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String rowkey = DocumentFamily.getRowkey(obj.getCreater(),obj.getPatientId());

            hbase.insertRecord(DocumentFamily.TableName, rowkey, DocumentFamily.Data,obj.getColumns(), obj.getValues());
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
    public Map<String, Object> getDocument(String rowkey)throws Exception{
        return hbase.getResultMap(DocumentFamily.TableName, rowkey);
    }

    /******************************** solr查询 ********************************************/
    /**
     * 获取素材列表
     */
    public List<Map<String, Object>> getList(String creatorId,String patientId,String dataFrom) throws Exception{

        return null;
    }
}
