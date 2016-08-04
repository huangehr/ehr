package com.yihu.ehr.medicalRecords.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecords.family.MedicalDrugFamily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
@Transactional
public class MedicalDrugDao extends HbaseDao {

    @Autowired
    HBaseUtil hbase;

    @Override
    protected String getTableName(){
        return MedicalDrugFamily.TableName;
    }


}
