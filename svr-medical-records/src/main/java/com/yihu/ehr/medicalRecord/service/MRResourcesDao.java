package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.model.MRRsResources;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */
public interface MRResourcesDao extends PagingAndSortingRepository<MRRsResources,String> {

}
