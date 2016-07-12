package com.yihu.ehr.user.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
public interface XRoleUserRepository extends PagingAndSortingRepository<RoleUser,Long> {
    @Query("select roleUser from RoleUser roleUser where roleUser.userId = :userId and roleUser.roleId = :roleId")
    RoleUser findRelation(@Param("userId") String userId,@Param("roleId") long roleId);
}
