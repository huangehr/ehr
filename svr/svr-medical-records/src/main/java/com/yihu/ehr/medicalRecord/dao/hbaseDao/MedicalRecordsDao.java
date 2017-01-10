package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecord.family.MedicalRecordsFamily;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalRecord;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
@Transactional
public class MedicalRecordsDao extends HbaseDao {

    @Autowired
    HBaseUtil hbase;

    @Override
    protected String getTableName(){
        return MedicalRecordsFamily.TableName;
    }

    /**
     * 行保存病历
     */
    public boolean save(MedicalRecord obj) throws Exception
    {
        hbase.insertRecord(getTableName(), obj.getRowkey(), MedicalRecordsFamily.Data,obj.getColumns(), obj.getValues());
        return true;
    }

    /**
     * 列修改病历, data列族
     */
    public boolean update(String rowkey, String column, String value) throws Exception
    {
        hbase.updateRecord(getTableName(), rowkey, MedicalRecordsFamily.Data,column,value);
        return true;
    }

    /**
     * 列修改病历, dynamic列族
     */
    public boolean updateDynamic(String rowkey, String column, String value) throws Exception
    {
        hbase.updateRecord(getTableName(), rowkey, MedicalRecordsFamily.Dynamic,column,value);
        return true;
    }

    /******************************** solr查询 ********************************************/
    /**
     * 获取病历列表
     */
    public List<Map<String, Object>> getList(String creatorId, String patientId, String dataFrom) {

        return null;
    }


}
