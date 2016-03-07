package com.yihu.ehr.api.esb.dao;

import com.yihu.ehr.api.esb.model.HosLog;
import com.yihu.ehr.api.esb.model.HosSqlTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by chenweida on 2016/3/2.
 */
public interface IHosSqlTaskDao extends JpaRepository<HosSqlTask, String> {
    @Query("select e from HosSqlTask e where e.systemCode = :systemCode and e.orgCode= :orgCode and e.status='0' ")
    List<HosSqlTask> findBySystemCodeAndOrgCode(@Param("systemCode")String systemCode, @Param("orgCode")String orgCode);
}
