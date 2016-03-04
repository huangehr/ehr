package com.yihu.ehr.api.esb.dao;

import com.yihu.ehr.api.esb.model.HosAcqTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by chenweida on 2016/3/2.
 */
public interface IHosAcqTackDao extends JpaRepository<HosAcqTask, String> {
    @Query("select e from HosAcqTask e where e.systemCode = :systemCode and e.orgCode= :orgCode and e.status='0'")
    List<HosAcqTask> findBySystemCodeAndOrgCode(@Param("systemCode") String systemCode, @Param("orgCode") String orgCode);
}
