package com.yihu.ehr.quota.dao;

import com.yihu.ehr.entity.quota.TjDataSave;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjDataSaveRepository extends PagingAndSortingRepository<TjDataSave, Long> {
    TjDataSave findByCode(String code);
}
