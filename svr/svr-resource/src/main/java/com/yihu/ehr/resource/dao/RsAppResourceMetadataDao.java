package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
public interface RsAppResourceMetadataDao extends PagingAndSortingRepository<RsAppResourceMetadata, String> {
     void deleteByAppResourceId(String appResourceId);

     @Query("select arm.resourceMetadataId from RsAppResourceMetadata arm where arm.appId = :appId and arm.valid = 1")
     List<String> findMetadataIdByAppId(@Param("appId") String appId);
}
