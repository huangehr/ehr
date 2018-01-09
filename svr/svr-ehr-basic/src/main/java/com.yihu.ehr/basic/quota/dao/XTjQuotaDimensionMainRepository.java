package com.yihu.ehr.basic.quota.dao;

import com.yihu.ehr.entity.quota.TjQuotaDimensionMain;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XTjQuotaDimensionMainRepository extends PagingAndSortingRepository<TjQuotaDimensionMain, Integer> {

    @Modifying
    @Query("delete from TjQuotaDimensionMain DimensionMain where DimensionMain.quotaCode = :quotaCode")
    int deleteByQuotaCode(@Param("quotaCode") String quotaCode);

    List<TjQuotaDimensionMain> findByQuotaCode(String quotaCode);

}
