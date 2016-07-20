package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalRecordDao extends PagingAndSortingRepository<MrMedicalRecordsEntity,String> {

    @Query("from MrMedicalRecordsEntity where patientId=?1 And doctorId=?2 order by medicalTime desc")
    List<MrMedicalRecordsEntity> findBypatientIdAndDoctorId(int patientId, int doctorId);

    @Query("from MrMedicalRecordsEntity where doctorId=?1 order by medicalTime desc")
    List<MrMedicalRecordsEntity> findByDoctorId(int doctorId);

    MrMedicalRecordsEntity findByid(int id);
}
