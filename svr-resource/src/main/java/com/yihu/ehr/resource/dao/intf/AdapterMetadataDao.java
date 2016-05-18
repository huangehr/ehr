package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsAdapterMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/5/17.
 */
public interface AdapterMetadataDao extends PagingAndSortingRepository<RsAdapterMetadata,String> {
    Iterable<RsAdapterMetadata> findBySchemaId(String schema_id);
}
