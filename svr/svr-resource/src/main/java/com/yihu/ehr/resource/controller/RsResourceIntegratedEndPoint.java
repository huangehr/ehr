package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceQuota;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResourceDefaultQuery;
import com.yihu.ehr.resource.service.*;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * Created by Sxy on 2017/08.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsResourceIntegrated", description = "资源综合查询数据服务接口")
public class RsResourceIntegratedEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsResourceIntegratedService resourcesIntegratedService;
    @Autowired
    private RsResourceService rsService;
    @Autowired
    private RsResourceMetadataService rsMetadataService;
    @Autowired
    private RsResourceDefaultQueryService resourcesDefaultQueryService;
    @Autowired
    private RsResourceQuotaService resourceQuotaService;

    @ApiOperation("综合查询档案数据列表树")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    public Envelop getMetadataList(
            @ApiParam(name = "filters", value = "过滤条件(name)", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) {
        return resourcesIntegratedService.getMetadataList(filters);
    }

    @ApiOperation("综合查询档案数据检索")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    public Envelop searchMetadataData(
            @ApiParam(name = "resourcesCode", value = "资源代码([\"code\"])")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metaData", value = "数据元([\"metadataId\"])")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构代码(orgCode)")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "appId", value = "应用ID(appId)")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "queryCondition", value = "查询条件[{\"andOr\":\"(AND)(OR)\",\"condition\":\"(<)(=)(>)\",\"field\":\"fieldName\",\"value\":\"value\"}]")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页(>0)")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行(>0)")
            @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesIntegratedService.searchMetadataData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
    }

    @ApiOperation("综合查询指标统计列表树")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    public Envelop getQuotaList(
            @ApiParam(name = "filters", value = "过滤条件(name)", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesIntegratedService.getQuotaList(filters);
    }

    @ApiOperation("综合查询视图保存")
    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    public Envelop updateResource(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resource\":\"objStr\",\"(metadatas)(quotas)\":\"[objStr]\",\"queryCondition\":\"([])({})\"})")
            @RequestParam(value="dataJson") String dataJson) {
        Envelop envelop = new Envelop();
        RsResource newResources = null;
        try {
            envelop.setSuccessFlg(false);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> paraMap = mapper.readValue(dataJson, Map.class);
            if (!paraMap.containsKey("resource")) {
                return envelop;
            }
            //处理资源视图
            String resource = mapper.writeValueAsString(paraMap.get("resource"));
            RsResource rsResources = toEntity(resource, RsResource.class);
            if(rsService.findByField("name", rsResources.getName()).size() > 0) {
                envelop.setErrorMsg("资源名称重复");
                return envelop;
            }
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
            /**
             * 根据资源数据类型保存相关数据元和搜索条件
             */
            if (newResources.getDataSource() == 1) { //档案数据
                //处理关联档案数据元
                if(!paraMap.containsKey("metadatas")) {
                    envelop.setErrorMsg("档案数据元不能为空");
                    return envelop;
                }
                String rsMetadatasStr = mapper.writeValueAsString(paraMap.get("metadatas"));
                RsResourceMetadata[] rsMetadatas = toEntity(rsMetadatasStr, RsResourceMetadata[].class);
                for (RsResourceMetadata rsMetadata : rsMetadatas) {
                    rsMetadata.setResourcesId(reId);
                    rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
                }
                List<RsResourceMetadata> rsMetadataList = rsMetadataService.saveMetadataBatch(rsMetadatas);
                if (rsMetadataList == null || rsMetadataList.size() == 0) {
                    /**
                     * 档案数据元关联失败，删除资源
                     */
                    rsService.delete(newResources);
                    envelop.setErrorMsg("档案数据元关联失败");
                    return envelop;
                }
                //处理默认搜索条件
                List<Map<String, String>> queryList = (List<Map<String, String>>) paraMap.get("queryCondition");
                String queryCondition = mapper.writeValueAsString(queryList);
                RsResourceDefaultQuery resourcesQuery = new RsResourceDefaultQuery();
                resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                resourcesQuery.setQuery(queryCondition);
                resourcesQuery.setResourcesId(reId);
                resourcesQuery.setResourcesType(1);
                RsResourceDefaultQuery newRsResourcesQuery = resourcesDefaultQueryService.saveResourceQuery(resourcesQuery);
                if (newRsResourcesQuery == null) {
                    /**
                     * 默认搜索条件保存失败，删除资源和关联档案数据元
                     */
                    rsMetadataService.deleteRsMetadataByResourceId(newResources.getId());
                    rsService.delete(newResources);
                    envelop.setErrorMsg("资源默认搜索条件保存失败");
                    return envelop;
                }
            } else if (newResources.getDataSource() == 2) { //统计指标
                //处理关联指标数据元
                if(!paraMap.containsKey("quotas")) {
                    envelop.setErrorMsg("指标数据元不能为空");
                    return envelop;
                }
                String rsQuotasStr = mapper.writeValueAsString(paraMap.get("quotas"));
                RsResourceQuota[] rsQuotas = toEntity(rsQuotasStr, RsResourceQuota[].class);
                for(RsResourceQuota resourceQuota : rsQuotas) {
                    resourceQuota.setResourceId(reId);
                    RsResourceQuota newResourceQuota = resourceQuotaService.save(resourceQuota);
                    if(newResourceQuota == null) {
                        /**
                         * 指标数据元关联失败，删除资源和已关联的指标数据元
                         */
                        resourceQuotaService.deleteByResourceId(newResources.getId());
                        rsService.delete(newResources);
                        envelop.setErrorMsg("指标数据元关联失败");
                        return envelop;
                    }
                }
                //处理默认搜索条件
                Map<String, Object> queryMap = (Map<String, Object>)paraMap.get("queryCondition");
                String queryCondition = mapper.writeValueAsString(queryMap);
                RsResourceDefaultQuery resourcesQuery = new RsResourceDefaultQuery();
                resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                resourcesQuery.setQuery(queryCondition);
                resourcesQuery.setResourcesId(reId);
                resourcesQuery.setResourcesType(2);
                RsResourceDefaultQuery newRsResourcesQuery = resourcesDefaultQueryService.saveResourceQuery(resourcesQuery);
                if (newRsResourcesQuery == null) {
                    /**
                     * 默认搜索条件保存失败，删除资源和关联指标数据元
                     */
                    resourceQuotaService.deleteByResourceId(newResources.getId());
                    rsService.delete(newResources);
                    envelop.setErrorMsg("资源默认搜索条件保存失败");
                    return envelop;
                }
            }
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            if(newResources != null) {
                /**
                 * 报异常则删除所有已保存的数据
                 */
                resourcesDefaultQueryService.deleteByResourcesId(newResources.getId());
                rsMetadataService.deleteRsMetadataByResourceId(newResources.getId());
                resourceQuotaService.deleteByResourceId(newResources.getId());
                rsService.delete(newResources);
            }
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @ApiOperation("综合查询搜索条件更新")
    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resourceId\":\"resourceId\",\"queryCondition\":\"([])({})\"})")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> paraMap = mapper.readValue(dataJson, Map.class);
        if(!paraMap.containsKey("resourceId") || !paraMap.containsKey("queryCondition")) {
            return envelop;
        }
        String resourceId = (String)paraMap.get("resourceId");
        RsResource rsResources = rsService.getResourceById(resourceId);
        if(rsResources != null) {
            RsResourceDefaultQuery rsResourcesQuery = resourcesDefaultQueryService.findByResourcesId(resourceId);
            String queryCondition = "";
            if(rsResources.getDataSource() == 1) {
                List<Map<String, String>> queryList = (List<Map<String, String>>)paraMap.get("queryCondition");
                queryCondition = mapper.writeValueAsString(queryList);
            }else {
                Map<String, Object> queryMap = (Map<String, Object>)paraMap.get("queryCondition");
                queryCondition = mapper.writeValueAsString(queryMap);
            }
            if(rsResourcesQuery == null) {
                rsResourcesQuery = new RsResourceDefaultQuery();
                rsResourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                rsResourcesQuery.setResourcesId(resourceId);
                rsResourcesQuery.setResourcesType(rsResources.getDataSource());
            }
            rsResourcesQuery.setQuery(queryCondition);
            resourcesDefaultQueryService.saveResourceQuery(rsResourcesQuery);
            envelop.setSuccessFlg(true);
        }else {
            envelop.setErrorMsg("资源不存在");
        }
        return envelop;
    }

}