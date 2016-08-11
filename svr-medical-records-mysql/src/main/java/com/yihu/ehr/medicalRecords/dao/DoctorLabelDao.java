package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
 */
public interface DoctorLabelDao extends PagingAndSortingRepository<MrLabelEntity,Integer> {

    MrLabelEntity findByDoctorIdAndLabel(String id, String label);

    @Query(value = "select * from mr_label where doctor_id = ?1 and label_type=?2 and lable like ?4",nativeQuery = true)
    List<MrLabelEntity> findByDoctorIdAndLabelType(String doctorId, String labelType,String label);

    @Query(value = "select * from mr_label where doctor_id = ?1 and label_type=?2 and label_class=?3 and lable like ?4",nativeQuery = true)
    List<MrLabelEntity> findByDoctorIdAndLabelTypeAndLabelClass(String doctorId, String labelType, Integer labelClass,String label);
}
