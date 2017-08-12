package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.ResourceQuota;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
public interface ResourceQuotaDao extends PagingAndSortingRepository<ResourceQuota,Integer> {

    @Query("delete from ResourceQuota rq where rq.resourceId = :resourceId")
    @Modifying
    int deleteByResourceId(@Param("resourceId") String resourceId);

    @Query("select rq.quotaChart from ResourceQuota rq where rq.quotaId = :quotaId")
    List<Integer> findQuotaChartByQuotaId(@Param("quotaId")Integer quotaId);
}
