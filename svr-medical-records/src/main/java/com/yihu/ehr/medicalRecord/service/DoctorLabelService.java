package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.DoctorLabelClassDao;
import com.yihu.ehr.medicalRecord.dao.DoctorLabelDao;
import com.yihu.ehr.medicalRecord.model.Entity.MrLabelClassEntity;
import com.yihu.ehr.medicalRecord.model.Entity.MrLabelEntity;
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

    /**
     * 新增标签类别
     * @param mrLabelClassEntity
     * @return
     */
    public  MrLabelClassEntity addDoctorLabelsClass(MrLabelClassEntity mrLabelClassEntity){
        doctorLabelClassDao.save(mrLabelClassEntity);
           return mrLabelClassEntity;
    }

    /**
     * 修改标签类别
     * @param mrLabelClassEntity
     * @return
     */
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

    /**
     * 删除标签类别
     * @param id
     * @return
     */
    public  boolean deleteDoctorLabelsClass(int id){
        doctorLabelClassDao.deleteById(id);
        return true;
    }

    /**
     * 获取医生标签类别
     * @param doctorId
     * @return
     */
    public List<MrLabelClassEntity> getDoctorLabelsClass(String doctorId){
        return  doctorLabelClassDao.findByDoctorId(doctorId);
    }


    /**
     * 新增医生标签
     * @param mrLabelEntity
     * @return
     */
    public  MrLabelEntity addDoctorLabels(MrLabelEntity mrLabelEntity){
        doctorLabelDao.save(mrLabelEntity);
        return mrLabelEntity;
    }

    /**
     * 修改医生标签
     * @param mrLabelEntity
     * @return
     */
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

    /**
     * 删除医生标签
     * @param id
     * @return
     */
    public  boolean deleteDoctorLabels(int id){
        doctorLabelDao.deleteById(id);
        return true;
    }

    /**
     * 获取医生标签
     * @param doctorId
     * @param labelType
     * @param labelClass
     * @return
     */
    public List<MrLabelEntity> getDoctorLabels(String doctorId,String labelType,String labelClass){
        if(labelClass!=null && labelClass.length()>0){
            return doctorLabelDao.findByDoctorIdAndLabelTypeAndLabelClass(doctorId,labelType,labelClass);
        }
        else{
            return  doctorLabelDao.findByDoctorIdAndLabelType(doctorId,labelType);
        }

    }

    /**
     * 标签使用次数+1
     * @param id
     */
    public void DoctorLabelUsed(int id){
        MrLabelEntity m=doctorLabelDao.findByid(id);
        if(m!=null){
            m.setUsageCount(m.getUsageCount()+1);
        }
    }

}
