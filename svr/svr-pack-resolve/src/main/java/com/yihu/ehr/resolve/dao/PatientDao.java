package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.entity.patient.DemographicInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/1/6.
 */
public interface PatientDao extends PagingAndSortingRepository<DemographicInfo, String> {
}
