package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by yww on 2016/7/7.
 */
public interface XRoleAppRelationRepository extends PagingAndSortingRepository<RoleAppRelation,Long> {
    @Query("select roleApp from RoleAppRelation roleApp where roleApp.appId = :appId and roleApp.roleId = :roleId")
    RoleAppRelation findRelation(@Param("appId") String appId,@Param("roleId") long roleId);
}
