package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.Authentication;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
public interface XAuthenticationRepository extends PagingAndSortingRepository<Authentication, Integer> {

}
