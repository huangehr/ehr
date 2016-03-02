package com.yihu.ehr.standard.dispatch.service;

import com.yihu.ehr.standard.standardsource.service.StandardSource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
public interface XDispatchLogRepository extends PagingAndSortingRepository<DispatchLog, String> {



}
