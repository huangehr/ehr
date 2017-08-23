package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.resource.client.ResourcesIntegratedClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @author Sxy
 * @created 2016.08.01 17:46
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceIntegrated", description = "资源综合查询数据服务接口", tags = {"资源管理-资源综合查询数据服务接口"})
public class ResourcesIntegratedController extends BaseController {

    @Autowired
    private ResourcesIntegratedClient resourcesIntegratedClient;

    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据列表树")
    public Envelop getMetadataList(
            @ApiParam(name="filters",value="过滤条件",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesIntegratedClient.getMetadataList(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
            if(resources != null) {
                envelop.setTotalCount(resources.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据检索")
    public Envelop searchMetadataData(
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
            @RequestParam(value = "size", required = false) Integer size) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesIntegratedClient.searchMetadataData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
            if(resources != null) {
                envelop.setTotalCount(resources.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    @ApiOperation("综合查询指标统计列表树")
    public Envelop getStatisticsList(
            @ApiParam(name="filters",value="过滤条件",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> quotas = resourcesIntegratedClient.getQuotaList(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(quotas);
            if(quotas != null) {
                envelop.setTotalCount(quotas.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    @ApiOperation("综合查询视图保存")
    public Envelop updateResource(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            envelop = resourcesIntegratedClient.updateResource(dataJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    @ApiOperation("综合查询搜索条件更新")
    public Envelop updateResourceQuery(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            envelop = resourcesIntegratedClient.updateResourceQuery(dataJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return envelop;
    }


}
