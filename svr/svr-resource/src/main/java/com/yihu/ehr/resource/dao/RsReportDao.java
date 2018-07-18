package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 资源报表 DAO
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
public interface RsReportDao extends PagingAndSortingRepository<RsReport, Integer> {

    @Query(" FROM RsReport rc WHERE rc.id <> :id AND rc.code = :code ")
    RsReport isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM RsReport rc WHERE rc.id <> :id AND rc.name = :name ")
    RsReport isUniqueName(@Param("id") Integer id, @Param("name") String name);

    RsReport findByCode(@Param("code") String code);

    List<RsReport> findByReportCategoryId(@Param("reportCategoryId") Integer reportCategoryId);

    @Query("FROM RsReport rc WHERE rc.status = 1 and rc.reportCategoryId =:reportCategoryId order by rc.showType asc")
    List<RsReport> findByReportCategoryIdAndStatus(@Param("reportCategoryId") Integer reportCategoryId);

    @Query("FROM RsReport rc WHERE rc.status = 1 and rc.id in (:ids)")
    List<RsReport> findByIds(@Param("ids") List<Integer> ids);

    @Query("select rc.position FROM RsReport rc WHERE rc.code = :code ")
    String findPositionByCode(@Param("code") String code);
}
