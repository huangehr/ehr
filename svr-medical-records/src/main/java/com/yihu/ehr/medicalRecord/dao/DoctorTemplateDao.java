package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorTemplateEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface DoctorTemplateDao extends PagingAndSortingRepository<MrDoctorTemplateEntity,String> {

    List<MrDoctorTemplateEntity> findBydoctorId(String id);

}
