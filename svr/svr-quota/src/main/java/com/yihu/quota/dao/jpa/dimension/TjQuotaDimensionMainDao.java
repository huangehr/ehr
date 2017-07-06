package com.yihu.quota.dao.jpa.dimension;

import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface TjQuotaDimensionMainDao extends PagingAndSortingRepository<TjQuotaDimensionMain, Long>, JpaSpecificationExecutor<TjQuotaDimensionMain> {
}
