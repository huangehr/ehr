package com.yihu.ehr.tj.dao;

import com.yihu.ehr.entity.tj.TjQuotaDimensionSlave;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XTjQuotaDimensionSlaveRepository extends PagingAndSortingRepository<TjQuotaDimensionSlave, Integer> {
    @Modifying
    @Query("delete from TjQuotaDimensionSlave DimensionSlave where DimensionSlave.quotaCode = :quotaCode")
    int deleteByQuotaCode(@Param("quotaCode") String quotaCode);
}
