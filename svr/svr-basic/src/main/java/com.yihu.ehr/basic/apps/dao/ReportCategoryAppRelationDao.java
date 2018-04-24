package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.report.ReportCategoryAppRelation;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/4/8.
 */
public interface ReportCategoryAppRelationDao  extends PagingAndSortingRepository<ReportCategoryAppRelation, Integer> {
    void deleteByAppId(String appId);
}
