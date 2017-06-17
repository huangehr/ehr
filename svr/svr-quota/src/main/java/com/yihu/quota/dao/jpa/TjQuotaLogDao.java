package com.yihu.quota.dao.jpa;

import com.yihu.quota.model.jpa.TjQuotaLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/2.
 */
public interface TjQuotaLogDao extends PagingAndSortingRepository<TjQuotaLog, Long>, JpaSpecificationExecutor< TjQuotaLog> {
}
