package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.model.RsResourcesQuery;
import com.yihu.ehr.resource.service.ResourceMetadataService;
import com.yihu.ehr.resource.service.ResourcesIntegratedService;
import com.yihu.ehr.resource.service.ResourcesDefaultQueryService;
import com.yihu.ehr.resource.service.ResourcesService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * Created by Sxy on 2017/08.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "resourceIntegrated", description = "资源综合查询数据服务接口")
public class ResourcesIntegratedEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourcesIntegratedService resourcesIntegratedService;
    @Autowired
    private ResourcesService rsService;
    @Autowired
    private ResourceMetadataService rsMetadataService;
    @Autowired
    private ResourcesDefaultQueryService resourcesDefaultQueryService;

    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据列表树")
    public List<Map<String, Object>> getMetadataList(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesIntegratedService.getMetadataList(filters);
    }

    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据检索")
    public List<Map<String, Object>> searchMetadataData(
            @ApiParam(name = "resourcesCode", value = "资源代码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metaData", value = "数据元")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "queryCondition", value = "查询条件")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesIntegratedService.searchMetadataData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
    }

    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    @ApiOperation("综合查询指标统计列表树")
    public List<Map<String, Object>> getQuotaList(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesIntegratedService.getQuotaList(filters);
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    @ApiOperation("综合查询视图保存")
    public Envelop updateResource(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) {
        Envelop envelop = new Envelop();
        RsResources newResources = null;
        try {
            envelop.setSuccessFlg(false);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> paraMap = mapper.readValue(dataJson, Map.class);
            if (!paraMap.containsKey("resource") || !paraMap.containsKey("metadatas")) {
                return envelop;
            }
            //处理资源视图
            String resource = mapper.writeValueAsString(paraMap.get("resource"));
            RsResources rsResources = toEntity(resource, RsResources.class);
            if (rsService.getResourceByCode(rsResources.getCode()) != null) {
                envelop.setErrorMsg("资源编码重复");
                return envelop;
            }
            /**
             * 资源ID
             */
            String reId = getObjectId(BizObject.Resources);
            rsResources.setId(reId);
            newResources = rsService.saveResource(rsResources);
            if (newResources == null) {
                envelop.setErrorMsg("资源保存失败");
                return envelop;
            }
            if (newResources.getDataSource() == 1) { //档案数据
                //处理关联数据元
                String metadatas = mapper.writeValueAsString(paraMap.get("metadatas"));
                RsResourceMetadata[] rsMetadatas = toEntity(metadatas, RsResourceMetadata[].class);
                for (RsResourceMetadata rsMetadata : rsMetadatas) {
                    rsMetadata.setResourcesId(reId);
                    rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
                }
                List<RsResourceMetadata> rsMetadataList = rsMetadataService.saveMetadataBatch(rsMetadatas);
                if (rsMetadataList == null || rsMetadataList.size() == 0) {
                    /**
                     * 数据元关联失败，删除资源
                     */
                    rsService.delete(newResources);
                    envelop.setErrorMsg("资源数据元关联失败");
                    return envelop;
                }
                //处理默认搜索条件
                List<Map<String, String>> queryList = (List<Map<String, String>>) paraMap.get("queryCondition");
                String queryCondition = mapper.writeValueAsString(queryList);
                RsResourcesQuery resourcesQuery = new RsResourcesQuery();
                resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                resourcesQuery.setQuery(queryCondition);
                resourcesQuery.setResourcesId(reId);
                RsResourcesQuery newRsResourcesQuery = resourcesDefaultQueryService.saveResourceQuery(resourcesQuery);
                if (newRsResourcesQuery == null) {
                    /**
                     * 默认搜索条件保存失败，删除资源和关联数据元
                     */
                    for (RsResourceMetadata rsResourceMetadata : rsMetadataList) {
                        rsMetadataService.delete(rsResourceMetadata);
                    }
                    rsService.delete(newResources);
                    envelop.setErrorMsg("资源默认搜索条件保存失败");
                    return envelop;
                }
            } else if (newResources.getDataSource() == 2) { //统计指标

            }
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            if(newResources != null) {
                resourcesDefaultQueryService.deleteByResourcesId(newResources.getId());
                rsMetadataService.deleteRsMetadataByResourceId(newResources.getId());
                rsService.delete(newResources);
            }
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    @ApiOperation("综合查询搜索条件更新")
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> paraMap = mapper.readValue(dataJson, Map.class);
        if(!paraMap.containsKey("resourceId") || !paraMap.containsKey("queryCondition")) {
            return envelop;
        }
        String resourceId = (String)paraMap.get("resourceId");
        RsResources rsResources = rsService.getResourceById(resourceId);
        if(rsResources != null) {
            RsResourcesQuery rsResourcesQuery = resourcesDefaultQueryService.findByResourcesId(resourceId);
            List<Map<String, String>> queryList = (List<Map<String, String>>)paraMap.get("queryCondition");
            String queryCondition = mapper.writeValueAsString(queryList);
            if(rsResourcesQuery == null) {
                rsResourcesQuery = new RsResourcesQuery();
                rsResourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                rsResourcesQuery.setResourcesId(resourceId);
            }
            rsResourcesQuery.setQuery(queryCondition);
            resourcesDefaultQueryService.saveResourceQuery(rsResourcesQuery);
            envelop.setSuccessFlg(true);
        }else {
            throw new Exception("资源不存在");
        }
        return envelop;
    }

}