package com.yihu.ehr.quota.dao;

import com.yihu.ehr.entity.quota.TjQuotaDataSource;
import com.yihu.ehr.entity.quota.TjQuotaLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XTjQuotaLogRepository extends PagingAndSortingRepository<TjQuotaLog, String> {

    @Query("select quotaLog from TjQuotaLog quotaLog where quotaLog.quotaCode = :quotaCode and quotaLog.endTime > :endTime  order by quotaLog.id desc ")
    List<TjQuotaLog> getRecentRecord(@Param("quotaCode") String quotaCode,@Param("endTime") Date endTime);

}
