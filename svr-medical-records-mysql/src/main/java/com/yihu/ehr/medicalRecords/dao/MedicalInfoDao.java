package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalInfoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/8/4.
 * 病历病情操作类
 */
public interface MedicalInfoDao extends PagingAndSortingRepository<MrMedicalInfoEntity,Integer> {


    List<MrMedicalInfoEntity> findByRecordId(String recordId);
}
