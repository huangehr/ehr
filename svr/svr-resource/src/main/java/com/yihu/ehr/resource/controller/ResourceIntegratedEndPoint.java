package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceDefaultParam;
import com.yihu.ehr.resource.model.RsResourceQuota;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.service.*;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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
    private ResourceBrowseService resourceBrowseService;
    @Autowired
    private RsResourceDefaultParamService resourceDefaultParamService;

    @Deprecated
    @ApiOperation("综合查询档案数据列表树")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    public Envelop getMetadataList(
            @ApiParam(name = "userResource", value = "授权资源", required = true)
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "roleId", value = "角色id", required = true)
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "filters", value = "过滤条件(name)")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List list = resourcesIntegratedService.getMetadataList(userResource, roleId, filters);
        return success(list);
    }

    @ApiOperation("综合查询档案数据分类列表")
    @RequestMapping(value = ServiceApi.Resources.IntCategory, method = RequestMethod.GET)
    public Envelop intCategory() throws Exception {
        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("baseInfo", new ArrayList<>());
        List<Map<String, Object>> list = resourcesIntegratedService.intCategory();
        return success(baseMap, list);
    }

    @ApiOperation("综合查询档案数据资源列表")
    @RequestMapping(value = ServiceApi.Resources.IntMetadata, method = RequestMethod.GET)
    public Envelop intMetadata(
            @ApiParam(name = "categoryId", value = "分类ID", required = true)
            @RequestParam(value = "categoryId") String categoryId,
            @ApiParam(name = "userResource", value = "授权资源", required = true)
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "roleId", value = "角色id", required = true)
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "filters", value = "过滤条件(name)")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Map<String, Object>> list = resourcesIntegratedService.intMetadata(categoryId, userResource, roleId, filters);
        return success(list);
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
       return resourceBrowseService.getCustomizeData(resourcesCode, metaData, orgCode, areaCode, queryCondition, page, size);
    }

    @ApiOperation("综合查询指标统计列表树")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    public Envelop getQuotaList(
            @ApiParam(name = "filters", value = "过滤条件(name)", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Map<String, Object>> list = resourcesIntegratedService.getQuotaList(filters);
        return success(list);
    }

    @ApiOperation("综合查询视图保存")
    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    public Envelop updateResource (
            @ApiParam(name="dataJson",value = "JSON对象参数({\"resource\":\"objStr\",\"(metadatas)(quotas)\":\"[objStr]\",\"queryCondition\":\"([])({})\"})", required = true)
            @RequestParam(value = "dataJson") String dataJson) throws Exception {
        RsResource newResources;
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
        if (rsResources.getDataSource() == 1) { //档案数据
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
            RsResourceDefaultParam rsResourceDefaultParam = null;
            if (paraMap.get("queryCondition") != null) {
                List<Map<String, String>> queryList = (List<Map<String, String>>) paraMap.get("queryCondition");
                if (!queryList.isEmpty()) {
                    String queryCondition = objectMapper.writeValueAsString(queryList);
                    rsResourceDefaultParam = new RsResourceDefaultParam();
                    rsResourceDefaultParam.setResourcesId(reId);
                    rsResourceDefaultParam.setResourcesCode(rsResources.getCode());
                    rsResourceDefaultParam.setParamKey("q");
                    rsResourceDefaultParam.setParamValue(queryCondition);
                }
            }
            newResources = resourcesIntegratedService.profileCompleteSave(rsResources, Arrays.asList(rsMetadatas), rsResourceDefaultParam);
            return success(newResources.getId());
        } else if (rsResources.getDataSource() == 2) { //统计指标
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
            RsResourceDefaultParam rsResourceDefaultParam = null;
            if (paraMap.get("queryCondition") != null) {
                Map<String, Object> queryMap = (Map<String, Object>)paraMap.get("queryCondition");
                if (!queryMap.isEmpty()) {
                    String queryCondition = objectMapper.writeValueAsString(queryMap);
                    rsResourceDefaultParam = new RsResourceDefaultParam();
                    rsResourceDefaultParam.setResourcesId(reId);
                    rsResourceDefaultParam.setResourcesCode(rsResources.getCode());
                    rsResourceDefaultParam.setParamKey("q");
                    rsResourceDefaultParam.setParamValue(queryCondition);
                }
            }
            newResources = resourcesIntegratedService.quotaCompleteSave(rsResources, Arrays.asList(rsQuotas), rsResourceDefaultParam);
            return success(newResources.getId());
        } else {
            return failed("资源类型有误");
        }
    }

    @ApiOperation("综合查询搜索条件更新")
    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    public Envelop customizeUpdate(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resourceId\":\"resourceId\",\"queryCondition\":\"([])({})\"})", required = true)
            @RequestParam(value = "dataJson") String dataJson) throws  Exception {
        Map<String, Object> paraMap = objectMapper.readValue(dataJson, Map.class);
        if (!paraMap.containsKey("resourceId") || !paraMap.containsKey("queryCondition")) {
            return failed("参数不完整，缺少resourceId或者queryCondition");
        }
        String resourceId = (String)paraMap.get("resourceId");
        RsResource rsResources = rsService.getResourceById(resourceId);
        if (rsResources != null) {
            RsResourceDefaultParam rsResourceDefaultParam = null;
            String queryCondition = (String)paraMap.get("queryCondition");
            if (rsResources.getDataSource() == 1) {
                if (!queryCondition.equals("[]")) {
                    rsResourceDefaultParam = new RsResourceDefaultParam();
                    rsResourceDefaultParam.setResourcesId(rsResources.getId());
                    rsResourceDefaultParam.setResourcesCode(rsResources.getCode());
                    rsResourceDefaultParam.setParamKey("q");
                    rsResourceDefaultParam.setParamValue(queryCondition);
                }
            } else {
                if (!queryCondition.equals("{}")) {
                    rsResourceDefaultParam = new RsResourceDefaultParam();
                    rsResourceDefaultParam.setResourcesId(rsResources.getId());
                    rsResourceDefaultParam.setResourcesCode(rsResources.getCode());
                    rsResourceDefaultParam.setParamKey("q");
                    rsResourceDefaultParam.setParamValue(queryCondition);
                }
            }
            if (rsResourceDefaultParam != null) {
                RsResourceDefaultParam resourceDefaultParam = resourceDefaultParamService.saveWithDel(rsResourceDefaultParam);
                return success(resourceDefaultParam);
            }
            return failed("条件不能为空");
        } else {
            return failed("资源不存在");
        }
    }

}