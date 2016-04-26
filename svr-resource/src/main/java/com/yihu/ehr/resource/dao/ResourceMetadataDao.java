package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/25.
 */
public interface ResourceMetadataDao extends PagingAndSortingRepository<RsResourceMetadata,String>{

}
