package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.RoleOrg;
import com.yihu.ehr.user.entity.RoleUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by janseny on 2017/10/18.
 */
public interface XRoleOrgRepository extends PagingAndSortingRepository<RoleOrg,Long> {

    @Query("select roleOrg from RoleOrg roleOrg where roleOrg.roleId = :roleId and roleOrg.orgCode = :orgCode")
    RoleOrg findRelation(@Param("roleId") Long roleId,@Param("orgCode") String orgCode);
}
