package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.resource.client.ResourcesCustomizeClient;
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
@Api(value = "resourceCustomize", description = "自定义资源服务接口", tags = {"资源管理-自定义资源服务接口"})
public class ResourcesCustomizeController extends BaseController {

    @Autowired
    private ResourcesCustomizeClient resourcesCustomizeClient;

    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源列表树")
    public Envelop getCustomizeList(
            @ApiParam(name="filters",value="过滤条件",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesCustomizeClient.getCustomizeList(filters);
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

    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeData, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源数据")
    public Envelop getCustomizeData(
            @ApiParam(name = "resourcesCode", value = "资源代码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metadata", value = "数据元")
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
            @RequestParam(value = "size", required = false) Integer size) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesCustomizeClient.getCustomizeData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
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

}
