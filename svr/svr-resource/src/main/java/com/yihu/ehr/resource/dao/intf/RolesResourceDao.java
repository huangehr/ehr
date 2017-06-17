package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsRolesResource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zdm on 2017/6/15
 */
public interface RolesResourceDao extends PagingAndSortingRepository<RsRolesResource,String> {
    RsRolesResource findByRolesId(String rolesId);
}
