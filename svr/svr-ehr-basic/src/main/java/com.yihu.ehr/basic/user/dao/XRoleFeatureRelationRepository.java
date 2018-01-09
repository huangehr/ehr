package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.RoleFeatureRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
public interface XRoleFeatureRelationRepository extends PagingAndSortingRepository<RoleFeatureRelation,Long> {
    @Query("select roleFeature from RoleFeatureRelation roleFeature where roleFeature.featureId = :featureId and roleFeature.roleId = :roleId")
    RoleFeatureRelation findRelation(@Param("featureId") long featureId,@Param("roleId") long roleId);

    @Query("select count(roleFeature.id) from RoleFeatureRelation roleFeature where roleFeature.featureId = 669 and roleFeature.roleId in (:roleId)")
    int findNumByRoleIds(@Param("roleId")List<Long> roleId);
}
