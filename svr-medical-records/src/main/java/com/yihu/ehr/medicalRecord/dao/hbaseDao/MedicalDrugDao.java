package com.yihu.ehr.medicalRecord.dao.hbaseDao;

import com.yihu.ehr.hbase.HBaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzp on 2016/8/1.
 */
@Service
@Transactional
public class MedicalDrugDao {

    @Autowired
    HBaseUtil hbase;




}
