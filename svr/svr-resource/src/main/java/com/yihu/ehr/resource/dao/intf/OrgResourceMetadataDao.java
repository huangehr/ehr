package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsOrgResourceMetadata;
import com.yihu.ehr.resource.model.RsRolesResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zdm on 2017/7/4
 */
public interface OrgResourceMetadataDao extends PagingAndSortingRepository<RsOrgResourceMetadata,String> {
     void deleteByOrganizationResourceId(String organizationResourceId);
}
