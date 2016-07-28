package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.DoctorLabelClassDao;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorLabelDao;
import com.yihu.ehr.medicalRecord.model.MrLabelClassEntity;
import com.yihu.ehr.medicalRecord.model.MrLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
@Transactional
@Service
public class DoctorLabelService {

    @Autowired
    DoctorLabelClassDao doctorLabelClassDao;
    @Autowired
    DoctorLabelDao doctorLabelDao;

    public  MrLabelClassEntity addDoctorLabelsClass(MrLabelClassEntity mrLabelClassEntity){
        doctorLabelClassDao.save(mrLabelClassEntity);
           return mrLabelClassEntity;
    }

    public  boolean updateDoctorLabelsClass(MrLabelClassEntity mrLabelClassEntity){
        MrLabelClassEntity m=doctorLabelClassDao.findByid(mrLabelClassEntity.getId());
        if(mrLabelClassEntity!=null && m!=null){
            m.setDoctorId(mrLabelClassEntity.getDoctorId());
            m.setLabelType(mrLabelClassEntity.getLabelType());
            m.setParentId(mrLabelClassEntity.getParentId());
            return true;
        }
        else
            return false;

    }

    public  boolean deleteDoctorLabelsClass(int id){
        doctorLabelClassDao.deleteById(id);
        return true;
    }

    public List<MrLabelClassEntity> getDoctorLabelsClass(String doctorId){
        return  doctorLabelClassDao.findByDoctorId(doctorId);
    }


    public  MrLabelEntity addDoctorLabels(MrLabelEntity mrLabelEntity){
        doctorLabelDao.save(mrLabelEntity);
        return mrLabelEntity;
    }

    public  boolean updateDoctorLabels(MrLabelEntity mrLabelEntity){
        MrLabelEntity m=doctorLabelDao.findByid(mrLabelEntity.getId());
        if(mrLabelEntity!=null && m!=null){
            m.setDoctorId(mrLabelEntity.getDoctorId());
            m.setLabelType(mrLabelEntity.getLabelType());
            m.setLabel(mrLabelEntity.getLabel());
            m.setLabelClass(mrLabelEntity.getLabelClass());
            return true;
        }
        else
            return false;

    }

    public  boolean deleteDoctorLabels(int id){
        doctorLabelDao.deleteById(id);
        return true;
    }

    public List<MrLabelEntity> getDoctorLabels(String doctorId,String labelType,String labelClass){
        if(labelClass!=null && labelClass.length()>0){
            return doctorLabelDao.findByDoctorIdAndLabelTypeAndLabelClass(doctorId,labelType,labelClass);
        }
        else{
            return  doctorLabelDao.findByDoctorIdAndLabelType(doctorId,labelType);
        }

    }

    public void DoctorLabelUsed(int id){
        MrLabelEntity m=doctorLabelDao.findByid(id);
        if(m!=null){
            m.setUsageCount(m.getUsageCount()+1);
        }
    }

}
