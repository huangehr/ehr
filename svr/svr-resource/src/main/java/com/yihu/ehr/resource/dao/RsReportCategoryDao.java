package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReportCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 资源报表分类 DAO
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
public interface RsReportCategoryDao extends CrudRepository<RsReportCategory, Integer> {

    @Query(" FROM RsReportCategory rc WHERE rc.pid = :pid ")
    List<RsReportCategory> getChildrenByPid(@Param("pid") Integer pid);

    @Query(" FROM RsReportCategory rc WHERE rc.pid = 0 ")
    List<RsReportCategory> getTopParents();

    @Query(" FROM RsReportCategory rc WHERE rc.id <> :id AND rc.code = :code ")
    RsReportCategory isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM RsReportCategory rc WHERE rc.pid = :id AND rc.name = :name ")
    RsReportCategory isUniqueName(@Param("id") Integer id, @Param("name") String name);

    @Query("from RsReportCategory rc where rc.id in(:ids)  ORDER BY rc.sortNo asc")
    List<RsReportCategory> findCategoryByIds(@Param("ids") List<Integer> ids);

    @Query("select rc.id from RsReportCategory rc where rc.pid =(select r.id from RsReportCategory r where r.code = :code) ORDER BY rc.sortNo asc")
    List<Integer> findCategoryIds(@Param("code") String code);

    @Query("select rc.code from RsReportCategory rc where rc.id = :id")
    String findCategoryCodeById(@Param("id") Integer id);

    @Query("select rc.id from RsReportCategory rc where rc.pid in (select r.id from RsReportCategory r where r.code in('businessMonitoringSystem','healthMonitorSystem')) ORDER BY rc.sortNo asc")
    List<Integer> findCategoryIdsByCodeList();
}
