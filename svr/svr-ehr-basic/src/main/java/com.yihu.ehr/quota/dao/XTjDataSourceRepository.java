package com.yihu.ehr.quota.dao;

import com.yihu.ehr.entity.quota.TjDataSource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjDataSourceRepository extends PagingAndSortingRepository<TjDataSource, Long> {
    TjDataSource findByCode(String code);
}
