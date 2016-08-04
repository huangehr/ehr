package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.DoctorLabelClassDao;
import com.yihu.ehr.medicalRecords.dao.DoctorLabelDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrLabelClassEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrLabelEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
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

    /************************ 医生标签 ******************************************************/
    /**
     * 保存医生标签，不存在则新增，存在则+1次数
     */
    public boolean saveDoctorLabel(String doctorId,String label){
        MrLabelEntity obj = doctorLabelDao.findByDoctorIdAndLabel(doctorId,label);

        //存在则次数+1
        if(obj!=null)
        {
            int num = obj.getUsageCount();
            obj.setUsageCount(num+1);

        }
        else{
            obj = new MrLabelEntity();
            obj.setUsageCount(1);
            obj.setLabel(label);
            obj.setDoctorId(doctorId);
            obj.setLabelClass(0); //暂时默认
            obj.setLabelType(EnumClass.LabelType.MedicalLabel); //暂时默认
        }
        doctorLabelDao.save(obj);

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


}
