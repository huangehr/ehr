package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleReportRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/8/22.
 */
public interface XRoleReportRelationRepository extends PagingAndSortingRepository<RoleReportRelation,Long> {

    List<RoleReportRelation> findByRsReportId(@Param("rsReportId") Long rsReportId);

    @Query("delete from RoleReportRelation roleReport where roleReport.roleId = :roleId")
    @Modifying
    int deleteByRoleId(@Param("roleId") Long roleId);
}
