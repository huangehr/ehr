package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface RsResourceDao extends PagingAndSortingRepository<RsResource, String> {

    RsResource findByCode(String code);
    RsResource findById(String id);
    long countByCategoryId(String categoryId);
    List<RsResource> findByCategoryId(String categoryId);
    List<RsResource> findByCategoryIdAndDataSource(String categoryId, Integer dataSource);
    @Query("SELECT rsResource FROM RsResource rsResource WHERE rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.name LIKE %:name%")
    List<RsResource> findByCategoryIdAndDataSourceAndName(@Param("categoryId") String categoryId, @Param("dataSource") Integer dataSource, @Param("name") String name);
}
