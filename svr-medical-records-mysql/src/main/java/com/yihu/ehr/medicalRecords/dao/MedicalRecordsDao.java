package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/8/4.
 * 病历表操作类
 */
public interface MedicalRecordsDao extends PagingAndSortingRepository<MrMedicalRecordsEntity,Integer> {

    MrMedicalRecordsEntity findById(String id);


}
