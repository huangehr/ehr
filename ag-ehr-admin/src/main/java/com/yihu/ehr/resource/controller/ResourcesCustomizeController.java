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
    @ApiOperation("获取资源列表树")
    public Envelop getMasterCategories(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesCustomizeClient.getCustomizeList(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


}
