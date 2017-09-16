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
    Iterable<RsAdapterMetadata> findBySchemeId(String scheme_id);

    @Query("from RsAdapterMetadata where schemeId = ?1")
    List<RsAdapterMetadata> findByScheme(String schemeId);

    @Query("from RsAdapterMetadata where schemeId = ?1 and srcDatasetCode = ?2")
    List<RsAdapterMetadata> findByDataset(String schemeId, String dataset);

    @Modifying
    @Query("delete from RsAdapterMetadata where schemeId = ?1")
    void deleteBySchemeId(String schemeId);

    List<RsAdapterMetadata> findBySchemeIdAndSrcDatasetCode(String schemeId, String srcDatasetCode);
}
