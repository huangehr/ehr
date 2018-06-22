package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsRolesResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zdm on 2017/6/15
 */
public interface RsRolesResourceDao extends PagingAndSortingRepository<RsRolesResource, String> {
    RsRolesResource findByResourceIdAndRolesId(String resourceId, String roleId);

    @Query("select ar.id FROM RsRolesResource ar WHERE ar.resourceId=:resourceId AND ar.rolesId=:rolesId ")
    List<String> findIdByResourceIdAndRolesId(@Param("resourceId")String resourceId, @Param("rolesId")String rolesId);

    List<RsRolesResource> findByResourceId(String resourceId);

    void deleteByResourceId(String resourceId);
}
