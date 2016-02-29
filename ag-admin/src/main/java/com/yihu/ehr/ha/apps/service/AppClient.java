package com.yihu.ehr.ha.apps.service;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MApp;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(MicroServices.AppMgr)
public interface AppClient {

    @RequestMapping(value = "/api/v1.0/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    Collection<MApp> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    /**
     * @param appJson
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/v1.0/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"}")
            @RequestParam(value = "app", required = false) String appJson);

    @RequestMapping(value = "/api/v1.0/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    MApp getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId);

    @RequestMapping(value = "/api/v1.0/apps", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    MApp updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "app", required = false) String appJson);

    @RequestMapping(value = "/api/v1.0/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    void deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId);

    @RequestMapping(value = "/api/v1.0/apps/status",method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    boolean updateStatus(
            @ApiParam(name = "app_id",value = "id",defaultValue = "")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "status",value = "状态",defaultValue = "")
            @RequestParam(value = "app_status") String appStatus);

    @RequestMapping(value = "/api/v1.0/apps/existence/app_id/{app_id}",method = RequestMethod.GET)
    @ApiOperation(value = "验证")
    boolean isAppExistence(
            @ApiParam(name = "app_id",value = "id",defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret",value = "",defaultValue = "")
            @RequestParam(value = "secret") String secret);

    @RequestMapping(value = "/api/v1.0/apps/existence/app_name/{app_name}",method = RequestMethod.GET)
    @ApiOperation(value = "验证")
    boolean isAppNameExists(
           @ApiParam(value = "app_name")
           @PathVariable(value = "app_name") String appName);

}
