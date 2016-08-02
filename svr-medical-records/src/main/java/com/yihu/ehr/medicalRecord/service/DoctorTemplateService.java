package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorTemplateDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class DoctorTemplateService {

    @Autowired
    DoctorTemplateDao dTDao;

    public List<MrDoctorTemplateEntity> getTemplateByDoctorId(String doctorId){

         return  dTDao.findBydoctorId(doctorId);
    }

    /**
     * 批量创建数据元
     */
    public boolean saveTemplate(String doctorId, List<Map<String, Object>> templateInfoList){

        Map<String, Object> map;

        List<MrDoctorTemplateEntity> templateList = dTDao.findBydoctorId(doctorId);
        dTDao.delete(templateList);

        for(int i=1; i<=templateInfoList.size(); i++){
            map = templateInfoList.get(i-1);

            MrDoctorTemplateEntity mt = new MrDoctorTemplateEntity();

            mt.setDoctorId((String) map.get("doctorId"));
            mt.setCode((String) map.get("code"));
            mt.setName((String) map.get("name"));

            dTDao.save(mt);
        }
        return true;
    }
}