package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterOrg")
@RestController
public class AdapterOrgController extends BaseRestController {

    @RequestMapping(value = "/adapterOrg",method = RequestMethod.GET)
    public Object getAdapterOrgByCode(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "api_version") String apiVersion,
                                      @ApiParam(name = "code", value = "代码")
                                      @RequestParam(value = "code") String code) {
        return null;
    }

    @RequestMapping(value = "/adapterOrgs", method = RequestMethod.GET)
    public String searchAdapterOrg(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "api_version") String apiVersion,
                                   @ApiParam(name = "", value = "机构代码")
                                   @RequestParam(value = "code") String code,
                                   @ApiParam(name = "name", value = "机构名称")
                                   @RequestParam(value = "name") String name,
                                   @ApiParam(name = "type", value = "类型")
                                   @RequestParam(value = "type") String type,
                                   @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                   @RequestParam(value = "page") int page,
                                   @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                   @RequestParam(value = "rows") int rows) {
        return null;
    }
}
