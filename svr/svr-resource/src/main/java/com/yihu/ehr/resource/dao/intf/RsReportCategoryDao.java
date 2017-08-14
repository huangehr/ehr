package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsReportCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * 资源报表分类 DAO
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
public interface RsReportCategoryDao extends PagingAndSortingRepository<RsReportCategory, Integer> {

    @Query(" FROM RsReportCategory rc WHERE rc.pid = :pid ")
    List<RsReportCategory> getChildrenByPid(@Param("pid") Integer pid);

    @Query(" FROM RsReportCategory rc WHERE rc.pid = null ")
    List<RsReportCategory> getTopParents();

    @Query(" FROM RsReportCategory rc WHERE rc.name LIKE CONCAT('%',:name,'%') ")
    List<RsReportCategory> getComboTreeData(@Param("name") String name);

}
