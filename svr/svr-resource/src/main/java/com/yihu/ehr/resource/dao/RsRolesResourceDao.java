package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsRolesResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zdm on 2017/6/15
 */
public interface RsRolesResourceDao extends PagingAndSortingRepository<RsRolesResource, String> {
    RsRolesResource findByResourceIdAndRolesId(String resourceId, String roleId);

    List<RsRolesResource> findByResourceId(String resourceId);

    void deleteByResourceId(String resourceId);
}
