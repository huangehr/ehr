package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2016/8/4.
 * 病历表操作类
 */
public interface MedicalInfoDao extends PagingAndSortingRepository<MrMedicalRecordsEntity,Integer> {

    MrMedicalRecordsEntity findById(int id);

    @Query(value = "select * from mr_medical_records where doctor_id = ?1 and patient_id=?2 order by create_time desc limit 1", nativeQuery = true)
    MrMedicalRecordsEntity getLastRecord(String doctorId, String patientId);
}
