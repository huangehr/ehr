package com.yihu.ehr.government.dao;


import com.yihu.ehr.entity.government.GovernmentMenuReportMonitorType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 政府平台菜单资源报表监测分类 DAO
 *
 * @author janseny
 * @created 2017年11月7日15:27:28
 */
public interface GovernmentMenuReportMonitorTypeDao extends CrudRepository<GovernmentMenuReportMonitorType, Integer> {

    void deleteByGovernmentMenuId(@Param("governmentMenuId") Integer governmentMenuId);

    @Query("select MonitorType.reportCategoryId from GovernmentMenuReportMonitorType MonitorType where MonitorType.governmentMenuId = :menuId")
    List<Integer> findMonitorTypeIdByGovernmentMenuId(@Param("menuId") Integer menuId);
}
