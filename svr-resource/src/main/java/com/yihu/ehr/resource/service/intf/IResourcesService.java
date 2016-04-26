package com.yihu.ehr.resource.service.intf;


import com.yihu.ehr.resource.common.DataGridResult;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsDimension;
import com.yihu.ehr.resource.model.RsResources;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by hzp on 20160419.
 */
public interface IResourcesService {

    RsResources createResource(RsResources resource);

    void updateResource(RsResources resource);

    void deleteResource(String id);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsResources> getResources(String sorts, int page, int size);
}
