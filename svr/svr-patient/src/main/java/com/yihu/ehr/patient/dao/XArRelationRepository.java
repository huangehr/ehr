package com.yihu.ehr.patient.dao;

import com.yihu.ehr.patient.service.arapply.ArRelation;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public interface XArRelationRepository extends PagingAndSortingRepository<ArRelation, Integer> {

}
