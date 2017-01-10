package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorDao extends PagingAndSortingRepository<MrDoctorsEntity,String> {

    MrDoctorsEntity findBydemographicId(String demographicId);

    MrDoctorsEntity findById(String id);


}
