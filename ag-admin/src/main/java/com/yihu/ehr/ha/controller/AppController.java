package com.yihu.ehr.ha.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.service.AppClient;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/app")
@RestController
public class AppController {
    @Autowired
    private AppClient appClient;

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    public Object getAppList(@ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
                             @RequestParam(value = "searchNm") Integer searchNm,
                             @ApiParam(name = "catalog", value = "类别", defaultValue = "")
                             @RequestParam(value = "catalog") Integer catalog,
                             @ApiParam(name = "status", value = "状态", defaultValue = "")
                             @RequestParam(value = "status") Integer status,
                             @ApiParam(name = "page", value = "当前页", defaultValue = "")
                             @RequestParam(value = "page") Integer page,
                             @ApiParam(name = "rows", value = "页数", defaultValue = "")
                             @RequestParam(value = "rows") Integer rows) {


        return appClient.getAppList(searchNm, catalog, status, page, rows);
    }

    @RequestMapping(value = "/app", method = RequestMethod.DELETE)
    public Object deleteApp(@ApiParam(name = "appId", value = "appid", defaultValue = "")
                            @RequestParam(value = "appId") String appId) {
        return appClient.deleteApp(appId);
    }

    @RequestMapping(value = "/appDetail", method = RequestMethod.GET)
    public Object getAppDetail(@ApiParam(name = "appId", value = "appid", defaultValue = "")
                               @RequestParam(value = "appId") String appId) {
        return appClient.getAppDetail(appId);
    }

    
}
