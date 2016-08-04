package com.yihu.ehr.medicalRecords.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.family.DocumentFamily;
import com.yihu.ehr.medicalRecords.model.DTO.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
public class DocumentDao extends HbaseDao {

    @Autowired
    HBaseUtil hbase;

    @Override
    protected String getTableName(){
        return DocumentFamily.TableName;
    }

    /**
     * 新增记录
     */
    public boolean insert(Document obj)throws Exception{

        if(obj.getCreater()!=null  && obj.getFileType()!=null && obj.getFileUrl()!=null) {
            String rowkey = DocumentFamily.getRowkey(obj.getCreater(),obj.getPatientId());

            hbase.insertRecord(getTableName(), rowkey, DocumentFamily.Data,obj.getColumns(), obj.getValues());
            return true;
        }
        else {
            Message.debug("信息不完整，"+obj.toString());
            return false;
        }
    }


    /******************************** solr查询 ********************************************/
    /**
     * 获取素材列表
     */
    public List<Map<String, Object>> getList(String creatorId,String patientId,String dataFrom) throws Exception{

        return null;
    }
}
