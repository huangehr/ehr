package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsRolesResource;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zdm on 2017/6/15
 */
public interface RsRolesResourceDao extends PagingAndSortingRepository<RsRolesResource, String> {
    RsRolesResource findByResourceIdAndRolesId(String resourceId, String roleId);
}
