package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.model.RsMonitorTypeReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 资源报表监测分类 DAO
 *
 * @author janseny
 * @created 2017年11月7日15:27:28
 */
public interface RsMonitorTypeReportDao extends PagingAndSortingRepository<RsMonitorTypeReport, Integer> {

    @Query("select rmtr from RsMonitorTypeReport rmtr where rmtr.reportId = :reportId and rmtr.rsReoportMonitorTypeId = :monitorTypeId")
    RsMonitorTypeReport findRelation(@Param("reportId") Integer reportId,@Param("monitorTypeId") Integer monitorTypeId);

    @Query("select rmtr.reportId from RsMonitorTypeReport rmtr where rmtr.rsReoportMonitorTypeId = :monitorTypeId")
    List<Integer> findReportIdByMonitorTypeId(@Param("monitorTypeId") Integer monitorTypeId);
}
