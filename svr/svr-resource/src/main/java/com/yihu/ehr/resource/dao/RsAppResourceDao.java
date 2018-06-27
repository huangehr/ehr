package com.yihu.ehr.resource.dao;

import com.yihu.ehr.entity.resource.RsAppResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
public interface RsAppResourceDao extends PagingAndSortingRepository<RsAppResource, String> {
    RsAppResource findByAppId(String appId);

    @Query("select ar.resourceId from RsAppResource ar where ar.appId = :appId")
    List<String> findResourceIdListByAppId(@Param("appId") String appId);

    @Query("select ar.id FROM RsAppResource ar WHERE ar.resourceId=:rsResourceId AND ar.appId=:appId ")
    List<String> findByrsResourceIdAndAppId(@Param("rsResourceId")String rsResourceId, @Param("appId")String appId);

    @Query("select ar FROM RsAppResource ar WHERE ar.resourceId IN (:rsResourceIds) AND ar.appId=:appId ")
    List<RsAppResource> findByrsResourceIds(@Param("rsResourceIds") String [] rsResourceIds,@Param("appId")String appId);


}
