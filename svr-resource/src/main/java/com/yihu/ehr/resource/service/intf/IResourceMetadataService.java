package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface IResourceMetadataService {
    RsResourceMetadata createResourceMetadata(RsResourceMetadata metadata);

    void updateResourceMetadata(RsResourceMetadata metadata);

    void deleteResourceMetadata(String id);

    void deleteRsMetadataByResourceId(String resourceId);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsResourceMetadata> getResourceMetadata(String sorts, int page, int size);
}
