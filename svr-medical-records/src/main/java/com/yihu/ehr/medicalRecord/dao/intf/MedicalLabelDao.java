package com.yihu.ehr.medicalRecord.dao.intf;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalLabelDao extends PagingAndSortingRepository<MrMedicalLabelEntity,Integer> {

    List<MrMedicalLabelEntity> findByrecordsId(String id);

    MrMedicalLabelEntity findByid(String id);
    void deleteByid(String id);

    @Query("select recordsId from MrMedicalLabelEntity r where r.label in (:label)")
    List<String> findByLabel(String[] label);
}
