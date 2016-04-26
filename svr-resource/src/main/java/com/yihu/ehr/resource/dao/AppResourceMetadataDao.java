package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/26.
 */
public interface AppResourceMetadataDao extends PagingAndSortingRepository<RsAppResourceMetadata,String> {
     void deleteByAppResourceId(String appResourceId);
}
