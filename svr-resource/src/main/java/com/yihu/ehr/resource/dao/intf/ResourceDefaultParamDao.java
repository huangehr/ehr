package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.ResourceDefaultParam;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface ResourceDefaultParamDao extends PagingAndSortingRepository<ResourceDefaultParam,String> {
    List<ResourceDefaultParam> findByResourcesCode(String resourcesCode);
}