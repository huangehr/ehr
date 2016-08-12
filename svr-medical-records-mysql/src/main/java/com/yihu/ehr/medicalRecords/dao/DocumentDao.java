package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
 */
public interface DocumentDao extends PagingAndSortingRepository<MrDocumentEntity,Integer> {

    MrDocumentEntity findById(String id);

    List<MrDocumentEntity> findByCreaterAndPatientId(@Param("creator") String creator,
                                                     @Param("patientId") String patientId,
                                                     Pageable pageable);

    @Query(value = "select * from mr_document m where m.id in (:ids)", nativeQuery = true)
    List<MrDocumentEntity> findByIds(@Param("ids") String[] ids);

}
