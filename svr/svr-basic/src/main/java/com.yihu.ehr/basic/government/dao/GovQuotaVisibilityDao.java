package com.yihu.ehr.basic.government.dao;

import com.yihu.ehr.basic.government.entity.GovQuotaVisibility;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 政府服务平台首页指标展示管理 Dao
 *
 * @author 张进军
 * @date 2018/7/5 17:44
 */
public interface GovQuotaVisibilityDao extends PagingAndSortingRepository<GovQuotaVisibility, Integer> {

    @Query(" FROM GovQuotaVisibility a WHERE a.id <> :id AND a.code = :code ")
    GovQuotaVisibility isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM GovQuotaVisibility a WHERE a.id <> :id AND a.name = :name ")
    GovQuotaVisibility isUniqueName(@Param("id") Integer id, @Param("name") String name);

    List<GovQuotaVisibility> findByType(String type);

    @Modifying
    @Query(" UPDATE GovQuotaVisibility SET isShow = :isShow WHERE code = :code ")
    GovQuotaVisibility updateStatus(@Param("code") String code, @Param("isShow") String isShow);
    
}
