package com.yihu.ehr.resource.service.intf;


import com.yihu.ehr.query.common.model.DataList;

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
    DataList getResources(String resourcesCode,String appId,String queryParams,Integer page,Integer size) throws Exception;
}
