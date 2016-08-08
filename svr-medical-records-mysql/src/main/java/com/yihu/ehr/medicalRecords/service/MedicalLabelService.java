package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalLabelDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
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
       return medicalLabelDao.findByRecordId(recordId);
    }

    /**
     * 批量保存病历标签
     */
    @Transactional
    public boolean saveMedicalLabel(String recordId,String doctorId,String[] labels){
        //清空病历标签
        medicalLabelDao.deleteByRecordId(recordId);
        //保存病历标签
        if(labels!=null && labels.length>0)
        {
            MrDoctorMedicalRecordsEntity record = doctorMedicalRecordDao.findByDoctorIdAndRecordId(doctorId,recordId);

            for(String label:labels)
            {
                MrMedicalLabelEntity obj = new MrMedicalLabelEntity();
                obj.setDoctorId(doctorId);
                obj.setLabel(label);
                obj.setRecordId(recordId);
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
                if (map.get(m.get(i).getRecordId()) != null) {
                    String k= m.get(i).getRecordId();
                    map.put(m.get(i).getRecordId(),map.get(k)+1);
                }
                else{
                    map.put(m.get(i).getRecordId(),1);
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

    /**
     * 通过关系id删除病历标签
     */
    public boolean deleteMedicalLabelById(Integer id){
        medicalLabelDao.delete(id);
        return true;
    }



}
