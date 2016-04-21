package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.service.intf.IResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/resources")
@Api(value = "resources", description = "资源服务接口")
public class ResourcesController {
    @Autowired
    private IResourcesService resourceService;



    /**
     *获取某数据集数据
     */
    @ApiOperation("获取某数据集数据")
    @RequestMapping(value = "/getDataset", method = RequestMethod.GET)
    public String getDataset(@ApiParam("datasetCode") @RequestParam(value = "datasetCode", required = true) String datasetCode,@ApiParam("orgCode") @RequestParam(value = "orgCode", required = true) String orgCode,
                                 @ApiParam("systemCode") @RequestParam(value = "systemCode", required = true) String systemCode) {
        try {
            //判断是否有权限

            return "";//resourceService.getDataset(datasetCode,orgCode);
        } catch (Exception e) {
            return "";
        }
    }
}
