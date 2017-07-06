package com.yihu.quota.dao.jpa;

import com.yihu.quota.model.jpa.TjQuota;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface TjQuotaDao extends PagingAndSortingRepository<TjQuota, Integer>, JpaSpecificationExecutor<TjQuota> {

}
