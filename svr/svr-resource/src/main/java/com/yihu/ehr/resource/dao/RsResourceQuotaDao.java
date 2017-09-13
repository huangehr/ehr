package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceQuota;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
public interface RsResourceQuotaDao extends PagingAndSortingRepository<RsResourceQuota,Integer> {

    @Query("delete from RsResourceQuota rq where rq.resourceId = :resourceId")
    @Modifying
    int deleteByResourceId(@Param("resourceId") String resourceId);

    @Query("select rq.quotaChart from RsResourceQuota rq where rq.quotaId = :quotaId and rq.resourceId = :resourceId")
    List<Integer> findQuotaChartByQuotaId(@Param("quotaId")Integer quotaId, @Param("resourceId") String resourceId);
}
