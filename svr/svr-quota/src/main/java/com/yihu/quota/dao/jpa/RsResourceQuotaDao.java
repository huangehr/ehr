package com.yihu.quota.dao.jpa;

import com.yihu.quota.model.jpa.RsResourceQuota;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/19.
 */
public interface RsResourceQuotaDao extends PagingAndSortingRepository<RsResourceQuota, Integer>, JpaSpecificationExecutor<RsResourceQuota> {
    @Query("select rq.quotaId from RsResourceQuota rq where rq.resourceId = :resourceId and rq.pid = :pid")
    List<String> fingQuotaRelation(@Param("resourceId") String resourceId, @Param("pid") Integer pid);

    @Query("select rq from RsResourceQuota rq where rq.resourceId = :resourceId and rq.pid = :pid")
    List<RsResourceQuota> findChildByPidAndResourceId(@Param("resourceId") String resourceId, @Param("pid") Integer pid);

    @Query(" FROM RsResourceQuota rq WHERE rq.resourceId = :resourceId and rq.pid = null ")
    List<RsResourceQuota> getTopParents(@Param("resourceId") String resourceId);

    @Query("select  rq FROM RsResourceQuota rq WHERE rq.resourceId = :resourceId and rq.quotaId = :quotaId")
    RsResourceQuota findByResourceIdAndQuotaId(@Param("resourceId")String resourceId, @Param("quotaId")Integer quotaId);
}
