package com.yihu.ehr.apps.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppRepository extends PagingAndSortingRepository<App, String> {



}
