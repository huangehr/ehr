package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReportView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 资源报表视图配置 DAO
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
public interface RsReportViewDao extends PagingAndSortingRepository<RsReportView, Integer> {

    List<RsReportView> findByReportId(Integer reportId);

    RsReportView findByReportIdAndResourceId(Integer reportId, String resourceId);

    @Modifying
    @Query(" DELETE FROM RsReportView rv WHERE rv.reportId = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);

}
