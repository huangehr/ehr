package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
@Transactional
public class MedicalRecordsDao {

    @Autowired
    HBaseUtil hbase;

    //新增病历


    //修改病历


    //删除病历




    /******************************** solr查询 ********************************************/
    /**
     * 获取病历列表
     */
    public List<Map<String, Object>> getList(String creatorId, String patientId, String dataFrom) {

        return null;
    }


}
