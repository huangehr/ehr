package com.yihu.ehr.ha.apps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.apps.service.AppClient;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0 )
@RestController
@Api(value = "app", description = "应用管理接口，用于接入应用管理", tags = {"应用管理接口"})
public class AppController extends BaseRestController {
    @Autowired
    private AppClient appClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    @ResponseHeader
    public List<MApp> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filter", value = "过滤器，规则参见说明文档", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletResponse response) throws Exception {

        return appClient.getApps(fields,filter,sort,size,page,response);
    }

    /**
     * @param name
     * @param catalog
     * @param url
     * @param description
     * @param tags
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "url", value = "url", defaultValue = "")
            @RequestParam(value = "url") String url,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "tags", value = "标记", defaultValue = "")
            @RequestParam(value = "tags") String tags,
            @ApiParam(name = "user_id", value = "用户", defaultValue = "")
            @RequestParam(value = "user_id") String userId) throws Exception {
      return appClient.createApp(name,catalog,url,description,tags,userId);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        return appClient.getApp(appId);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
            @ApiParam(name = "app_id", value = "appId", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status,
            @ApiParam(name = "url", value = "url", defaultValue = "")
            @RequestParam(value = "url") String url,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "tags", value = "标记", defaultValue = "")
            @RequestParam(value = "tags") String tags) throws Exception {

        return appClient.updateApp(appId,name,catalog,status,url,description,tags);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    public void deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        appClient.deleteApp(appId);
    }

}
