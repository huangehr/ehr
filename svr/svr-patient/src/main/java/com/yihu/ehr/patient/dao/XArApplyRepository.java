package com.yihu.ehr.patient.dao;

import com.yihu.ehr.patient.service.arapply.ArApply;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public interface XArApplyRepository extends PagingAndSortingRepository<ArApply, Integer> {

}
