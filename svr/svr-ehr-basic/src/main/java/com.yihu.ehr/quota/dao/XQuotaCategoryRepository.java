package com.yihu.ehr.quota.dao;

import com.yihu.ehr.entity.report.QuotaCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/8/31.
 */
public interface XQuotaCategoryRepository extends PagingAndSortingRepository<QuotaCategory, Integer> {
    @Query("select qc from QuotaCategory qc")
    List<QuotaCategory> getAllQuotaCategory();

    @Query("select qc from QuotaCategory qc where qc.parentId = :parentId")
    List<QuotaCategory> searchByParentId(@Param("parentId") Integer parentId);

    @Query("select qc from QuotaCategory qc where qc.name = :name")
    List<QuotaCategory> searchByName(@Param("name") String name);

    @Query("select qc from QuotaCategory qc where qc.code = :code")
    List<QuotaCategory> searchByCode(@Param("code") String code);

    @Query("select qc from QuotaCategory qc where qc.parentId = 1")
    List<QuotaCategory> getQuotaCategoryOfChild();

    @Query("select qc from QuotaCategory qc where qc.parentId != 0 ")
    List<QuotaCategory> getQuotaCategoryChild();
}
