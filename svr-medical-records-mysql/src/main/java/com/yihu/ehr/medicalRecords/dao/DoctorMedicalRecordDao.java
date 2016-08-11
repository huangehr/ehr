package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2016/7/14.
 */
public interface DoctorMedicalRecordDao extends PagingAndSortingRepository<MrDoctorMedicalRecordsEntity,String> {

    MrDoctorMedicalRecordsEntity findByDoctorIdAndRecordId(String doctorId, String recordId);

    @Query(value = "select * from mr_doctor_medical_records where doctor_id = ?1 and patient_id=?2 and record_type='"+ EnumClass.RecordType.Online+"' limit 1", nativeQuery = true)
    MrDoctorMedicalRecordsEntity getLastRecord(String doctorId, String patientId);

    void deleteByRecordId( String recordId);
}
