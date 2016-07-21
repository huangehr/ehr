package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MedicalReporImgtDao extends PagingAndSortingRepository<MrMedicalReportImgEntity,String> {


    List<MrMedicalReportImgEntity> findByreportIdOrderBySort(int reportId);

}
