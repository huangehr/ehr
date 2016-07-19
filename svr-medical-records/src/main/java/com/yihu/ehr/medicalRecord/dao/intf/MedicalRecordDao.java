package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalRecordDao extends PagingAndSortingRepository<MrMedicalRecordsEntity,String> {

    List<MrMedicalRecordsEntity> findBypatientIdAndDoctorId(int patientId, int doctorId);

    MrMedicalRecordsEntity findByid(int id);
}
