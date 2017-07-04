package com.yihu.ehr.health.dao;

import com.yihu.ehr.entity.health.HealthBusiness;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */
public interface XHealthBusinessRepository extends PagingAndSortingRepository<HealthBusiness, Integer> {
    @Query("select business from HealthBusiness business")
    List<HealthBusiness> getAllHealthBusiness();

    @Query("select business from HealthBusiness business where business.parentId = :parentId")
    List<HealthBusiness> searchByParentId(@Param("parentId") Integer parentId);

    @Query("select business from HealthBusiness business where business.name = :name")
    List<HealthBusiness> searchByName(@Param("name") String name);

    @Query("select business from HealthBusiness business where business.code = :code")
    List<HealthBusiness> searchByCode(@Param("code") String code);

    @Query("select business from HealthBusiness business where business.parentId = 1")
    List<HealthBusiness> getHealthBusinessOfChild();
}
