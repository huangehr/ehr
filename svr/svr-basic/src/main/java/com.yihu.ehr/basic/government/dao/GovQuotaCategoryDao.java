package com.yihu.ehr.basic.government.dao;

import com.yihu.ehr.basic.government.entity.GovQuotaCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 政府服务平台首页指标分类 Dao
 *
 * @author 张进军
 * @date 2018/7/5 17:44
 */
public interface GovQuotaCategoryDao extends PagingAndSortingRepository<GovQuotaCategory, Integer> {

    @Query(" FROM GovQuotaCategory a WHERE a.id <> :id AND a.code = :code ")
    GovQuotaCategory isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM GovQuotaCategory a WHERE a.id <> :id AND a.name = :name ")
    GovQuotaCategory isUniqueName(@Param("id") Integer id, @Param("name") String name);

}
