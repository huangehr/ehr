package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalDraftEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalDraftDao extends PagingAndSortingRepository<MrMedicalDraftEntity,String> {

    List<MrMedicalDraftEntity> findBydoctorIdAndType(int id, String type);

}
