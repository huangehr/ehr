package com.yihu.ehr.quota.dao;


import com.yihu.ehr.entity.quota.TjQuotaChart;
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
public interface XTjQuotaChartRepository extends PagingAndSortingRepository<TjQuotaChart, Integer> {


    @Query("select quotaChart from TjQuotaChart quotaChart where quotaChart.quotaCode = :quotaCode order by quotaChart.id desc ")
    List<TjQuotaChart> getByQuotaCode(@Param("quotaCode") String quotaCode);

    @Modifying
    @Query("delete from TjQuotaChart TjQuotaChart where TjQuotaChart.quotaCode = :quotaCode")
    int deleteByQuotaCode(@Param("quotaCode") String quotaCode);

    @Query("select tc.chartId from TjQuotaChart tc where tc.quotaCode = :quotaCode")
    List<Integer> findByQuotaCode(@Param("quotaCode") String quotaCode);
}
