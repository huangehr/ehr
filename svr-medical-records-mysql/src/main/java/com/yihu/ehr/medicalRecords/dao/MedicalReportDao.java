package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalReportEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by hzp on 2016/7/14.
 */
public interface MedicalReportDao extends PagingAndSortingRepository<MrMedicalReportEntity,Integer> {

    MrMedicalReportEntity findByRecordIdAndId(String recordId,int reportId);

    @Modifying
    @Query("delete from MrDocumentEntity md " +
            "where md.id in (" +
            "      select mr.fileId " +
            "        from MrDocumentRelationEntity mr " +
            "       where mr.ownerType = '0' " +
            "         and mr.ownerId =:reportId)" )
    void deleteFileById(@Param("reportId") String reportId);

    void deleteById(int reportId);

}
