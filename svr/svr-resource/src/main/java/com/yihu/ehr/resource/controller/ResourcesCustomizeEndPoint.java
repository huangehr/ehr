package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.ResourceMetadataService;
import com.yihu.ehr.resource.service.ResourcesCustomizeService;
import com.yihu.ehr.resource.service.ResourcesService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * Created by lyr on 2016/5/4.
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
            @ApiParam(name = "appId", value = "机构代码")
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
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> paraMap = mapper.readValue(dataJson, Map.class);
            if(!paraMap.containsKey("resource") || !paraMap.containsKey("metadatas")) {
                return envelop;
            }
            //处理资源视图
            String resource = paraMap.get("resource");
            RsResources rsResources = toEntity(resource, RsResources.class);
            String reId = getObjectId(BizObject.Resources);
            rsResources.setId(reId);
            RsResources newResources = rsService.saveResource(rsResources);
            if(newResources == null) {
                throw new Exception("自定义资源保存失败");
            }
            //处理数据元
            String metadatas = paraMap.get("metadatas");
            RsResourceMetadata[] rsMetadata = toEntity(metadatas, RsResourceMetadata[].class);
            for (RsResourceMetadata metadata : rsMetadata) {
                metadata.setResourcesId(reId);
                metadata.setId(getObjectId(BizObject.ResourceMetadata));
            }
            List<RsResourceMetadata> metadataList = rsMetadataService.saveMetadataBatch(rsMetadata);
            if(metadataList == null && metadataList.size() <= 0) {
                throw new Exception("自定义资源数据元保存失败");
            }
            //处理默认搜索条件

        }catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }
}