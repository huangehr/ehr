package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsResource;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by hzp on 20160419.
 */
public interface IResourcesService {

    RsResource saveResource(RsResource resource);

    void deleteResource(String id);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsResource> getResources(String sorts, int page, int size);

    List<RsResource> findByCategoryId(String categoryId);

    RsResource getResourceById(String id);
}
