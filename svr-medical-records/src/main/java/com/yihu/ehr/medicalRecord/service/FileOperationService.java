package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorDraftDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrFileDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/26.
 */

@Service
@Transactional
public class FileOperationService{

    @Autowired
    MrFileDao fileDao;

    public MrFileEntity save(MrFileEntity fileEntity){

        return fileDao.save(fileEntity);
    }


}