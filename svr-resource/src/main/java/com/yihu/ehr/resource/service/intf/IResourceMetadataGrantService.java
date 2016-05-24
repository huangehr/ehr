package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by LYR-WORK on 2016/4/26.
 */
public interface IResourceMetadataGrantService {
    RsAppResourceMetadata grantRsMetadata(RsAppResourceMetadata  rsAppMetadata);

    List<RsAppResourceMetadata> grantRsMetadataBatch(List<RsAppResourceMetadata> rsAppMetadataList);

    void deleteRsMetadataGrant(String id);

    Page<RsAppResourceMetadata> getAppRsMetadataGrant(String sorts, int page, int size);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    RsAppResourceMetadata getRsMetadataGrantById(String id);
}
