package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

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

}
