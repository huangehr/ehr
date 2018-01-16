package com.yihu.ehr.medicalRecord.dao;
import com.yihu.ehr.medicalRecord.model.Entity.MrMedicalLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalLabelDao extends PagingAndSortingRepository<MrMedicalLabelEntity,Integer> {

    List<MrMedicalLabelEntity> findByRecordsId(String id);

    @Query("from MrMedicalLabelEntity where recordsId= ?1 AND  label= ?2 ")
    List<MrMedicalLabelEntity> findByRecordsIdAndLabel(String RecordsId, String label);


    void deleteByRecordsId(String recordsId);

    @Query("select m from MrMedicalLabelEntity m where m.label in (:labels)")
    List<MrMedicalLabelEntity> findByLabels(@Param("labels") String[] labels);

    @Query(value="select RECORDS_ID from mr_medical_label where ?1",nativeQuery=true)
    List<Integer> findByLabelst(String label);
}
