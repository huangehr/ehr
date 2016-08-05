package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/8/4.
 * 病历表操作类
 */
public interface MedicalRecordsDao extends PagingAndSortingRepository<MrMedicalRecordsEntity,Integer> {

    MrMedicalRecordsEntity findById(int id);

    @Query(value = "select * from mr_medical_records where first_record_id = ?1 or id=?1 order by create_time asc", nativeQuery = true)
    List<MrMedicalRecordsEntity> findRelatedRecord(String recordId);

    @Query(value = "select * from mr_medical_records where doctor_id = ?1 and patient_id=?2 order by create_time desc limit 1", nativeQuery = true)
    MrMedicalRecordsEntity getLastRecord(String doctorId, String patientId);
}
