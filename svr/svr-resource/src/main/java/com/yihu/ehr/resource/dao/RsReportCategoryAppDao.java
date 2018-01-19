package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.ReportCategoryAppRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/11/24.
 */
public interface RsReportCategoryAppDao extends CrudRepository<ReportCategoryAppRelation, Integer> {

    @Query
    @Modifying
    void deleteByReportCategoryIdAndAppId(@Param("reportCategoryId") Integer reportCategoryId, @Param("appId") String appId);

    @Query("select relation.appId from ReportCategoryAppRelation relation where relation.reportCategoryId = :reportCategoryId")
    List<String> findAppIdByCategory(@Param("reportCategoryId") Integer reportCategoryId);
}
