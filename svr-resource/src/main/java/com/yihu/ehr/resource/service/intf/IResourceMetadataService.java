package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface IResourceMetadataService {
    RsResourceMetadata saveResourceMetadata(RsResourceMetadata metadata);

    List<RsResourceMetadata> saveMetadataBatch(RsResourceMetadata[] metadataArray);

    void deleteResourceMetadata(String id);

    void deleteRsMetadataByResourceId(String resourceId);

    /**
     * 获取资源数据元
     *
     * @param id String Id
     * @return RsResourceMetadata
     */
    RsResourceMetadata getRsMetadataById(String id);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsResourceMetadata> getResourceMetadata(String sorts, int page, int size);
}
