package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.model.RsRolesResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zdm on 2017/6/16
 */
public interface RsRolesResourceMetadataDao extends PagingAndSortingRepository<RsRolesResourceMetadata, String> {
     void deleteByRolesResourceId(String rolesResourceId);
}
