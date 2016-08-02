package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorDraftDao extends PagingAndSortingRepository<MrDoctorDraftEntity,String> {

    @Query(value = "select * from mr_doctor_draft where doctor_id = ?1 and type = ?2 order by usage_count desc limit 10", nativeQuery = true)
    List<MrDoctorDraftEntity> findBydoctorId(int id, String type);

    MrDoctorDraftEntity findByid(int id);

    MrDoctorDraftEntity findBydoctorIdAndTypeAndContent(String doctorId, String Type, String content);

    List<MrDoctorDraftEntity> findByidIn(List id);
}
