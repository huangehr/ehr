package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorMedicalRecordDao extends PagingAndSortingRepository<MrDoctorMedicalRecordsEntity,String> {

    List<MrDoctorMedicalRecordsEntity> findBydoctorIdAndRecordId(String doctorId, int recordId);

    List<MrDoctorMedicalRecordsEntity> findBydoctorId(String doctorId);

    List<MrDoctorMedicalRecordsEntity> findByrecordId(int recordId);
}
