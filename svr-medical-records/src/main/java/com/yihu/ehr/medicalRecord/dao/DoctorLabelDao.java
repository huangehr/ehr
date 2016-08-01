package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrLabelEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
public interface DoctorLabelDao extends PagingAndSortingRepository<MrLabelEntity,Integer> {

    MrLabelEntity findByid(int id);

    void deleteById(int id);

    List<MrLabelEntity> findByDoctorIdAndLabelType(String doctorId,String labelType);
    List<MrLabelEntity> findByDoctorIdAndLabelTypeAndLabelClass(String doctorId,String labelType,String labelClass);
}
