package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceQuota;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResourceDefaultQuery;
import com.yihu.ehr.resource.service.*;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by Progr1mmer on 2017/08.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsResourceIntegratedEndPoint", description = "资源综合查询", tags = {"资源服务-资源综合查询"})
public class ResourceIntegratedEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceIntegratedService resourcesIntegratedService;
    @Autowired
    private RsResourceService rsService;
    @Autowired
    private RsResourceDefaultQueryService resourcesDefaultQueryService;

    @ApiOperation("综合查询档案数据列表树")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    public Envelop getMetadataList(
            @ApiParam(name = "userResource", value = "授权资源")
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "roleId", value = "角色id")
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "filters", value = "过滤条件(name)")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = resourcesIntegratedService.getMetadataList(userResource, roleId, filters);
        return envelop;
    }

    @ApiOperation("综合查询档案数据检索")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    public Envelop searchMetadataData(
            @ApiParam(name = "resourcesCode", value = "资源编码([\"code\"])")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metaData", value = "数据元([\"metadataId\"])")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构编码(orgCode)")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "areaCode", value = "地区编码(areaCode)")
            @RequestParam(value = "areaCode") String areaCode,
            @ApiParam(name = "queryCondition", value = "查询条件[{\"andOr\":\"(AND)(OR)\",\"condition\":\"(<)(=)(>)\",\"field\":\"fieldName\",\"value\":\"value\"}]")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页(>0)")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行(>0)")
            @RequestParam(value = "size", required = false) Integer size) throws Exception{
        return resourcesIntegratedService.searchMetadataData(resourcesCode, metaData, orgCode, areaCode, queryCondition, page, size);
    }

    @ApiOperation("综合查询指标统计列表树")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    public Envelop getQuotaList(
            @ApiParam(name = "filters", value = "过滤条件(name)", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesIntegratedService.getQuotaList(filters);
    }

    @ApiOperation("综合查询视图保存")
    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop updateResource (
            @ApiParam(name="dataJson",value="JSON对象参数({\"resource\":\"objStr\",\"(metadatas)(quotas)\":\"[objStr]\",\"queryCondition\":\"([])({})\"})")
            @RequestBody String dataJson) throws IOException {
        RsResource newResources = null;
        Map<String, Object> paraMap = objectMapper.readValue(dataJson, Map.class);
        if (!paraMap.containsKey("resource")) {
            return failed("resource不能为空");
        }
        //处理资源视图
        String resource = objectMapper.writeValueAsString(paraMap.get("resource"));
        RsResource rsResources = toEntity(resource, RsResource.class);
        if (rsService.findByField("name", rsResources.getName()).size() > 0) {
            return failed("资源名称重复");
        }
        if (rsService.getResourceByCode(rsResources.getCode()) != null) {
            return failed("资源编码重复");
        }
        /**
         * 资源ID
         */
        String reId = getObjectId(BizObject.Resources);
        rsResources.setId(reId);
        /**
         * 根据资源数据类型保存相关数据元和搜索条件
         */
        if (newResources.getDataSource() == 1) { //档案数据
            //处理关联档案数据元
            if (!paraMap.containsKey("metadatas")) {
                return failed("档案数据元不能为空");
            }
            String rsMetadatasStr = objectMapper.writeValueAsString(paraMap.get("metadatas"));
            RsResourceMetadata[] rsMetadatas = toEntity(rsMetadatasStr, RsResourceMetadata[].class);
            if (rsMetadatas == null || rsMetadatas.length <= 0) {
                //档案数据元为空，删除资源
                return failed("档案数据元不能为空");
            }
            for (RsResourceMetadata rsMetadata : rsMetadatas) {
                rsMetadata.setResourcesId(reId);
                rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
            }
            //处理默认搜索条件
            List<Map<String, String>> queryList = (List<Map<String, String>>) paraMap.get("queryCondition");
            String queryCondition = objectMapper.writeValueAsString(queryList);
            RsResourceDefaultQuery resourcesQuery = new RsResourceDefaultQuery();
            resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
            resourcesQuery.setQuery(queryCondition);
            resourcesQuery.setResourcesId(reId);
            resourcesQuery.setResourcesType(1);
            newResources = resourcesIntegratedService.profileCompleteSave(rsResources, Arrays.asList(rsMetadatas), resourcesQuery);
            return success(newResources.getId());
        } else if (newResources.getDataSource() == 2) { //统计指标
            //处理关联指标数据元
            if (!paraMap.containsKey("quotas")) {
                return failed("指标数据元不能为空");
            }
            String rsQuotasStr = objectMapper.writeValueAsString(paraMap.get("quotas"));
            RsResourceQuota[] rsQuotas = toEntity(rsQuotasStr, RsResourceQuota[].class);
            if (rsQuotas == null || rsQuotas.length <= 0 ) {
                return failed("指标数据元不能为空");
            }
            for (RsResourceQuota resourceQuota : rsQuotas) {
                resourceQuota.setResourceId(reId);
            }
            //处理默认搜索条件
            Map<String, Object> queryMap = (Map<String, Object>)paraMap.get("queryCondition");
            String queryCondition = objectMapper.writeValueAsString(queryMap);
            RsResourceDefaultQuery resourcesQuery = new RsResourceDefaultQuery();
            resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
            resourcesQuery.setQuery(queryCondition);
            resourcesQuery.setResourcesId(reId);
            resourcesQuery.setResourcesType(2);
            newResources = resourcesIntegratedService.quotaCompleteSave(rsResources, Arrays.asList(rsQuotas), resourcesQuery);
            return success(newResources.getId());
        } else {
            return failed("资源类型有误");
        }
    }

    @ApiOperation("综合查询搜索条件更新")
    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resourceId\":\"resourceId\",\"queryCondition\":\"([])({})\"})")
            @RequestBody String dataJson) throws  Exception {
        Map<String, Object> paraMap = objectMapper.readValue(dataJson, Map.class);
        if (!paraMap.containsKey("resourceId") || !paraMap.containsKey("queryCondition")) {
            return failed("参数不完整，缺少resourceId或者queryCondition");
        }
        String resourceId = (String)paraMap.get("resourceId");
        RsResource rsResources = rsService.getResourceById(resourceId);
        if (rsResources != null) {
            RsResourceDefaultQuery rsResourcesQuery = resourcesDefaultQueryService.findByResourcesId(resourceId);
            String queryCondition;
            if (rsResources.getDataSource() == 1) {
                //List<Map<String, String>> queryList = (List<Map<String, String>>)paraMap.get("queryCondition");
                //queryCondition = mapper.writeValueAsString(queryList);
                queryCondition = (String)paraMap.get("queryCondition");
            } else {
                //Map<String, Object> queryMap = (Map<String, Object>)paraMap.get("queryCondition");
                //queryCondition = mapper.writeValueAsString(queryMap);
                queryCondition = (String)paraMap.get("queryCondition");
            }
            if (rsResourcesQuery == null) {
                rsResourcesQuery = new RsResourceDefaultQuery();
                rsResourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
                rsResourcesQuery.setResourcesId(resourceId);
                rsResourcesQuery.setResourcesType(rsResources.getDataSource());
            }
            rsResourcesQuery.setQuery(queryCondition);
            RsResourceDefaultQuery newRsResourceDefaultQuery = resourcesDefaultQueryService.saveResourceQuery(rsResourcesQuery);
            return success(newRsResourceDefaultQuery);
        } else {
            return failed("资源不存在");
        }
    }

}