package com.yihu.ehr.quota.dao;

import com.yihu.ehr.entity.quota.TjQuota;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjQuotaRepository extends PagingAndSortingRepository<TjQuota, Long> {
    TjQuota findByCode(String code);
}
