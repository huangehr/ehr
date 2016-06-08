package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsAdapterMetadata;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/5/17.
 */
public interface AdapterMetadataDao extends PagingAndSortingRepository<RsAdapterMetadata,String> {
    Iterable<RsAdapterMetadata> findBySchemaId(String schema_id);

    @Query("from RsAdapterMetadata where schemaId = ?1")
    List<RsAdapterMetadata> findBySchema(String schemaId);

    @Modifying
    @Query("delete from RsAdapterMetadata where schemaId = ?1")
    void deleteBySchemaId(String schemaId);
}
