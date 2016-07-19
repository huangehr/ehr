package com.yihu.ehr.medicalRecord.dao.intf;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalLabelDao extends PagingAndSortingRepository<MrMedicalLabelEntity,Integer> {

    List<MrMedicalLabelEntity> findByrecordsId(Integer id);

    @Query("from MrMedicalLabelEntity where recordsId= ?1 AND  label= ?2 ")
    List<MrMedicalLabelEntity> findByRecordsIdAndLabel(Integer RecordsId,String label);

    MrMedicalLabelEntity findByid(String id);
    void deleteByid(String id);

    @Query("select recordsId from MrMedicalLabelEntity r where r.label in (:label)")
    List<String> findByLabel(String[] label);
}
