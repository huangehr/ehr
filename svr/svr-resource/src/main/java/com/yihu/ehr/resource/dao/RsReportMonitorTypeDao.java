package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReportMonitorType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 资源报表监测分类 DAO
 *
 * @author janseny
 * @created 2017年11月7日15:27:28
 */
public interface RsReportMonitorTypeDao extends CrudRepository<RsReportMonitorType, Integer> {

    @Query(" FROM RsReportMonitorType rc WHERE rc.id <> :id AND rc.name = :name ")
    RsReportMonitorType isUniqueName(@Param("id") Integer id, @Param("name") String name);

}
