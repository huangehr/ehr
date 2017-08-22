package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.RoleReportRelation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by wxw on 2017/8/22.
 */
public interface XRoleReportRelationRepository extends PagingAndSortingRepository<RoleReportRelation,Long> {

    @Query("delete from RoleReportRelation roleReport where roleReport.roleId = :roleId")
    @Modifying
    int deleteByRoleId(@Param("roleId") Long roleId);
}
