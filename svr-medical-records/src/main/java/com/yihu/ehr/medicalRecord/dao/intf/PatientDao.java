package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface PatientDao extends PagingAndSortingRepository<MrPatientsEntity,Integer> {

    MrPatientsEntity findBydemographicId(String demographicId);

    void deleteBydemographicId(String demographicId);

    MrPatientsEntity findByphone(String phone);

    void deleteByphone(String phone);

    MrPatientsEntity findByappUidAndAppPatientId(String AppUid, String AppPatientId);
}
