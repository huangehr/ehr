package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hzp on 2016/7/14.
 */
public interface PatientDao extends PagingAndSortingRepository<MrPatientsEntity,Integer> {

    MrPatientsEntity findById(String id);

}
