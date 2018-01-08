package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleOrg;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by janseny on 2017/10/18.
 */
public interface XRoleOrgRepository extends PagingAndSortingRepository<RoleOrg,Long> {

    @Query("select roleOrg from RoleOrg roleOrg where roleOrg.roleId = :roleId and roleOrg.orgCode = :orgCode")
    RoleOrg findRelation(@Param("roleId") Long roleId,@Param("orgCode") String orgCode);
}
