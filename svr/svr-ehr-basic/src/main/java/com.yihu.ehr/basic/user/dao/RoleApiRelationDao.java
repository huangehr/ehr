package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yww on 2016/7/8.
 */
public interface RoleApiRelationDao extends PagingAndSortingRepository<RoleApiRelation, Long> {

    @Query("SELECT roleApi FROM RoleApiRelation roleApi WHERE roleApi.apiId = :apiId AND roleApi.roleId = :roleId")
    RoleApiRelation findRelation(@Param("apiId") long apiId, @Param("roleId") long roleId);

    void deleteByApiIdAndRoleId(long apiId, long roleId);

    List<RoleApiRelation> findByRoleId(long roleId);

    void deleteByApiId(long id);

    void deleteByRoleId(long roleId);

}
