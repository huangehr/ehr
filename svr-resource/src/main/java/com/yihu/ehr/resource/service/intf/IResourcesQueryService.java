package com.yihu.ehr.resource.service.intf;


import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Created by hzp on 20160422.
 */
public interface IResourcesQueryService {



    /**
     * 获取资源
     * @param resourcesCode
     * @param appId
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    Page<Map<String,Object>> getResources(String resourcesCode,String appId,String queryParams,Integer page,Integer size) throws Exception;
}
