package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsOrgResource;
import com.yihu.ehr.resource.model.RsRolesResource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zdm on 2017/7/4
 */
public interface OrgResourceDao extends PagingAndSortingRepository<RsOrgResource,String> {
    RsOrgResource findByOrganizationId(String organizationId);
}
