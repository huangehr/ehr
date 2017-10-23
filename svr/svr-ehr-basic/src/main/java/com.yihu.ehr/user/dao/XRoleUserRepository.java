package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.RoleUser;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    void deleteByUserId(String userId);

    @Query("select roleUser.roleId from RoleUser roleUser where roleUser.userId = :userId")
    List<Long> findRoleIdByUserId(@Param("userId") String userId);
}
