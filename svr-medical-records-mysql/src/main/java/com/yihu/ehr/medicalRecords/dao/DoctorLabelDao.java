package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrLabelEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
 */
public interface DoctorLabelDao extends PagingAndSortingRepository<MrLabelEntity,Integer> {

    MrLabelEntity findByDoctorIdAndLabel(String id, String label);

    List<MrLabelEntity> findByDoctorIdAndLabelType(String doctorId, String labelType);

    List<MrLabelEntity> findByDoctorIdAndLabelTypeAndLabelClass(String doctorId, String labelType, Integer labelClass);
}
