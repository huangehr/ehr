package com.yihu.ehr.medicalRecords.dao;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalDrugEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hzp on 2016/8/5.
 */
public interface MedicalDrugDao extends PagingAndSortingRepository<MrMedicalDrugEntity,Integer> {

    List<MrMedicalDrugEntity> findByRecordId(String id);

    void deleteByRecordId(String recordId);

    @Query("select m from MrMedicalLabelEntity m where m.label in (:labels)")
    List<MrMedicalLabelEntity> findByLabels(@Param("labels") String[] labels);

}
