package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalReportDao extends PagingAndSortingRepository<MrMedicalReportEntity,String> {

    List<MrMedicalReportEntity> findByrecordsId(int id);

    MrMedicalReportEntity findByid(int id);

}
