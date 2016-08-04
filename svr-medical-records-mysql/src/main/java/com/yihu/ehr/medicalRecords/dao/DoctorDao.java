package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2016/7/14.
 */
public interface DoctorDao extends PagingAndSortingRepository<MrDoctorsEntity,String> {

    MrDoctorsEntity findBydemographicId(String demographicId);

    MrDoctorsEntity findById(String id);


}
