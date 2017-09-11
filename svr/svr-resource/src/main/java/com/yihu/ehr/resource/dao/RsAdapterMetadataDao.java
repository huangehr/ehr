package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAdapterMetadata;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/5/17.
 */
public interface RsAdapterMetadataDao extends PagingAndSortingRepository<RsAdapterMetadata, String> {
    Iterable<RsAdapterMetadata> findBySchemaId(String schema_id);

    @Query("from RsAdapterMetadata where schemaId = ?1")
    List<RsAdapterMetadata> findBySchema(String schemaId);

    @Query("from RsAdapterMetadata where schemaId = ?1 and srcDatasetCode = ?2")
    List<RsAdapterMetadata> findByDataset(String schemaId,String dataset);

    @Modifying
    @Query("delete from RsAdapterMetadata where schemaId = ?1")
    void deleteBySchemaId(String schemaId);

    List<RsAdapterMetadata> findBySchemaIdAndSrcDatasetCode(String schemaId,String srcDatasetCode);
}
