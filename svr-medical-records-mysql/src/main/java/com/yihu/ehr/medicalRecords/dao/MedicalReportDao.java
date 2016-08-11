package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalReportEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface MedicalReportDao extends PagingAndSortingRepository<MrMedicalReportEntity,Integer> {

    MrMedicalReportEntity findByRecordIdAndId(String recordId,int reportId);

    List<MrMedicalReportEntity> findByRecordId(String recordId);

    void deleteById(int reportId);

    void deleteByRecordId(String recordId);
}
