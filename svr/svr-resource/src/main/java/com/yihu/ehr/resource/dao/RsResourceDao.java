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

    //不受权限控制(JKZL超级管理员)
    List<RsResource> findByCategoryIdAndDataSource(String categoryId, Integer dataSource);
    @Query("SELECT rsResource FROM RsResource rsResource WHERE rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.name LIKE %:name%")
    List<RsResource> findByCategoryIdAndDataSourceAndName(
            @Param("categoryId") String categoryId,
            @Param("dataSource") Integer dataSource,
            @Param("name") String name);

    //受权限控制
    @Query("SELECT rsResource FROM RsResource rsResource WHERE (rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.id IN (:ids)) OR (rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.grantType = :grantType)")
    List<RsResource> findByCategoryIdAndDataSourceAndIdsOrGrantType(
            @Param("categoryId") String categoryId,
            @Param("dataSource") Integer dataSource,
            @Param("ids") String [] ids,
            @Param("grantType") String grantType);
    @Query("SELECT rsResource FROM RsResource rsResource WHERE ((rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.id IN (:ids)) OR (rsResource.categoryId = :categoryId AND rsResource.dataSource = :dataSource AND rsResource.grantType = :grantType)) AND rsResource.name LIKE %:name%")
    List<RsResource> findByCategoryIdAndDataSourceAndIdsOrGrantTypeAndName(
            @Param("categoryId") String categoryId,
            @Param("dataSource") Integer dataSource,
            @Param("ids") String [] ids,
            @Param("grantType") String grantType,
            @Param("name") String name);
}
