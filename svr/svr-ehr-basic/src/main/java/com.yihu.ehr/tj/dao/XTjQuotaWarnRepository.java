package com.yihu.ehr.tj.dao;

import com.yihu.ehr.entity.tj.TjQuotaLog;
import com.yihu.ehr.entity.tj.TjQuotaWarn;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/06/30
 */
public interface XTjQuotaWarnRepository extends PagingAndSortingRepository<TjQuotaWarn, Long> {

    List<TjQuotaWarn> findByUserId(String userId);

}
