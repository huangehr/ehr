package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorDao extends PagingAndSortingRepository<MrDoctorsEntity,Integer> {

    MrDoctorsEntity findBydemographicId(String demographicId);

    void deleteBydemographicId(String id);

}
