package com.yihu.ehr.basic.quota.dao;

import com.yihu.ehr.entity.quota.TjQuotaCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/8/31.
 */
public interface XTjQuotaCategoryRepository extends PagingAndSortingRepository<TjQuotaCategory, Integer> {
    @Query("select qc from TjQuotaCategory qc")
    List<TjQuotaCategory> getAllQuotaCategory();

    @Query("select qc from TjQuotaCategory qc where qc.parentId = :parentId")
    List<TjQuotaCategory> searchByParentId(@Param("parentId") Integer parentId);

    @Query("select qc from TjQuotaCategory qc where qc.name = :name")
    List<TjQuotaCategory> searchByName(@Param("name") String name);

    @Query("select qc from TjQuotaCategory qc where qc.code = :code")
    List<TjQuotaCategory> searchByCode(@Param("code") String code);

    @Query("select qc from TjQuotaCategory qc where qc.parentId = 1")
    List<TjQuotaCategory> getQuotaCategoryOfChild();

    @Query("select qc from TjQuotaCategory qc where qc.parentId != 0 ")
    List<TjQuotaCategory> getQuotaCategoryChild();
}
