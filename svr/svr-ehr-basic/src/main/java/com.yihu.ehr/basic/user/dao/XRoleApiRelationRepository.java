package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by yww on 2016/7/8.
 */
public interface XRoleApiRelationRepository extends PagingAndSortingRepository<RoleApiRelation,Long> {
    @Query("select roleApi from RoleApiRelation roleApi where roleApi.apiId = :apiId and roleApi.roleId = :roleId")
    RoleApiRelation findRelation(@Param("apiId") long apiId,@Param("roleId") long roleId);
}
