package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.EnumClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorMedicalRecordDao extends PagingAndSortingRepository<MrDoctorMedicalRecordsEntity,String> {

    List<MrDoctorMedicalRecordsEntity> findBydoctorIdAndRecordId(String doctorId, int recordId);

    List<MrDoctorMedicalRecordsEntity> findBydoctorId(String doctorId);

    List<MrDoctorMedicalRecordsEntity> findByrecordId(int recordId);

    List<MrDoctorMedicalRecordsEntity> findBydoctorIdAndPatientId(String doctorId,String patientId);

    MrDoctorMedicalRecordsEntity findByrecordIdAndIsCreator(int recordId, String isCreator);

    @Query(value = "select * from mr_doctor_medical_records where doctor_id = ?1 and patient_id=?2 and record_type='"+ EnumClass.RecordType.Online+"' limit 1", nativeQuery = true)
    MrDoctorMedicalRecordsEntity getLastRecord(String doctorId,String patientId);
}
