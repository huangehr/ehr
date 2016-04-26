package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
public interface IResourceGrantService {

    RsAppResource grantResource(RsAppResource rsAppResource);

    List<RsAppResource> grantResourceBatch(List<RsAppResource> appRsList);

    void deleteResourceGrant(String id);

    Page<RsAppResource> getAppResourceGrant(String sorts, int page, int size);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;
}
