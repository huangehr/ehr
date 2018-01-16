package com.yihu.ehr.api.esb.dao;

import com.yihu.ehr.api.esb.model.HosLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by chenweida on 2016/3/15.
 */
public interface IHosLogStatusDao extends JpaRepository<HosLogStatus, String> {
    @Query("from HosLogStatus e where e.systemCode = :systemCode and e.orgCode= :orgCode and e.status='1'")
    List<HosLogStatus> findBySystemCodeAndOrgCodeAndStatus(@Param("orgCode") String orgCode,@Param("systemCode") String systemCode);

}
