package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface MedicalLabelDao extends PagingAndSortingRepository<MrMedicalLabelEntity,Integer> {

    List<MrMedicalLabelEntity> findByRecordsId(String id);

    void deleteByRecordsId(String recordsId);

    @Query("select m from MrMedicalLabelEntity m where m.label in (:labels)")
    List<MrMedicalLabelEntity> findByLabels(@Param("labels") String[] labels);

}
