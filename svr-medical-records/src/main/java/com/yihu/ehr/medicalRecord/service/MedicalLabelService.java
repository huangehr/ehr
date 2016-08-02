package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.dao.DoctorLabelDao;
import com.yihu.ehr.medicalRecord.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.MedicalLabelDao;
import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.Entity.MrLabelEntity;
import com.yihu.ehr.medicalRecord.model.Entity.MrMedicalLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */
@Service
public class MedicalLabelService {

    @Autowired
    DoctorLabelService doctorLabelService;

    @Autowired
    MedicalLabelDao medicalLabelDao;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    /**
     * 获取病历标签
     */
    public List<MrMedicalLabelEntity> getMedicalLabelByRecordId(String recordId){
       return medicalLabelDao.findByRecordsId(recordId);
    }

    /**
     * 批量保存病历标签
     */
    @Transactional
    public boolean saveMedicalLabel(String recordId,String doctorId,List<String> list){
        //清空病历标签
        medicalLabelDao.deleteByRecordsId(recordId);
        //保存病历标签
        if(list!=null && list.size()>0)
        {
            MrDoctorMedicalRecordsEntity record = doctorMedicalRecordDao.findByDoctorIdAndRecordId(doctorId,recordId);

            for(String label:list)
            {
                MrMedicalLabelEntity obj = new MrMedicalLabelEntity();
                obj.setDoctorId(doctorId);
                obj.setLabel(label);
                obj.setRecordsId(recordId);
                medicalLabelDao.save(obj);

                //医生标签
                doctorLabelService.saveDoctorLabel(doctorId,label);
            }
        }
        return true;
    }

    /**
     * 通过标签获取病历
     */
    public List<String> getRecordIdByLabels(String...Lable){
        List<MrMedicalLabelEntity>m=medicalLabelDao.findByLabels(Lable);
        Map<String,Integer>map=new HashMap<>();
        for(int i=0;i<m.size();i++) {
            if (m.get(i) != null) {
                if (map.get(m.get(i).getRecordsId()) != null) {
                    String k= m.get(i).getRecordsId();
                    map.put(m.get(i).getRecordsId(),map.get(k)+1);
                }
                else{
                    map.put(m.get(i).getRecordsId(),1);
                }

            }
        }
        List<String>list=new ArrayList<>();
        for(String key:map.keySet()){
            if(map.get(key)==Lable.length){
                list.add(key);
            }
        }
       return list;
    }

}
