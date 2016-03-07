package com.yihu.ehr.api.esb.dao;

import com.yihu.ehr.api.esb.model.HosEsbMiniInstallLog;
import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by chenweida on 2016/3/2.
 */
public interface IHosEsbMiniReleaseDao extends JpaRepository<HosEsbMiniRelease, String> {
    @Query("select e from HosEsbMiniRelease e where e.systemCode = :systemCode ")
    List<HosEsbMiniRelease> findBySystemCode(@Param("systemCode")String systemCode) throws Exception;
}
