package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.model.RsResourcesQuery;
import com.yihu.ehr.resource.service.ResourceMetadataService;
import com.yihu.ehr.resource.service.ResourcesCustomizeService;
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
@Api(value = "resourceCustomize", description = "自定义资源服务接口")
public class ResourcesCustomizeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourcesCustomizeService resourcesCustomizeService;
    @Autowired
    private ResourcesService rsService;
    @Autowired
    private ResourceMetadataService rsMetadataService;
    @Autowired
    private ResourcesDefaultQueryService resourcesDefaultQueryService;

    /**
     * Map<String, Object>
     * @param filters
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.Resources.CustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源列表树")
    public List<Map<String, Object>> getCustomizeList(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesCustomizeService.getCustomizeList(filters);
    }

    @RequestMapping(value = ServiceApi.Resources.CustomizeData, method = RequestMethod.GET)
    @ApiOperation("获取自定义资数据")
    public List<Map<String, Object>> getCustomizeData(
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
        return resourcesCustomizeService.getCustomizeData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
    }

    @RequestMapping(value = ServiceApi.Resources.CustomizeUpdate, method = RequestMethod.POST)
    @ApiOperation("自定义资源视图保存")
    @Transactional
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> paraMap = mapper.readValue(dataJson, Map.class);
        if(!paraMap.containsKey("resource") || !paraMap.containsKey("metadatas")) {
            return envelop;
        }
        //处理资源视图
        String resource = mapper.writeValueAsString(paraMap.get("resource"));
        RsResources rsResources = toEntity(resource, RsResources.class);
        if(rsService.getResourceByCode(rsResources.getCode()) != null ) {
            throw new Exception("资源编码重复");
        }
        /**
         * 资源ID
         */
        String reId = getObjectId(BizObject.Resources);
        rsResources.setId(reId);
        RsResources newResources = rsService.saveResource(rsResources);
        if(newResources == null) {
            throw new Exception("自定义资源保存失败");
        }
        //处理数据元
        String metadatas = mapper.writeValueAsString(paraMap.get("metadatas"));
        RsResourceMetadata[] rsMetadatas = toEntity(metadatas, RsResourceMetadata[].class);
        for (RsResourceMetadata rsMetadata : rsMetadatas) {
            rsMetadata.setResourcesId(reId);
            rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
        }
        List<RsResourceMetadata> metadataList = rsMetadataService.saveMetadataBatch(rsMetadatas);
        if(metadataList == null || metadataList.size() <= 0) {
            throw new Exception("自定义资源数据元保存失败");
        }
        //处理默认搜索条件
        List<Map<String, String>> queryList = (List<Map<String, String>>)paraMap.get("queryCondition");
        if(queryList.size() > 0) {
            String queryCondition = mapper.writeValueAsString(queryList);
            RsResourcesQuery resourcesQuery = new RsResourcesQuery();
            resourcesQuery.setId(getObjectId(BizObject.ResourcesDefaultQuery));
            resourcesQuery.setQuery(queryCondition);
            resourcesQuery.setResourcesId(reId);
            RsResourcesQuery newRsResourcesQuery = resourcesDefaultQueryService.saveResourceQuery(resourcesQuery);
            if(newRsResourcesQuery == null) {
                throw new Exception("自定义资源默认搜索条件保存失败");
            }
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }
}