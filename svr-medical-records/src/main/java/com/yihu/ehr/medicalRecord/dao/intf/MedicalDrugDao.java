package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalDrugEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/19.
 */
public interface MedicalDrugDao  extends PagingAndSortingRepository<MrMedicalDrugEntity,Integer> {

    List<MrMedicalDrugEntity> findByrecordsId(Integer recordsId);

    void deleteByRecordsId(Integer recordsId);
}
