package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorTemplateEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface DoctorTemplateDao extends PagingAndSortingRepository<MrDoctorTemplateEntity,String> {

    List<MrDoctorTemplateEntity> findBydoctorId(String id);

}
