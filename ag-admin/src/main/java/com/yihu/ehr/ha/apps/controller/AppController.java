package com.yihu.ehr.ha.apps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.apps.service.AppClient;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.app.MAppDetail;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion +"/appcon")
@RestController
public class AppController extends BaseRestController {
    @Autowired
    private AppClient appClient;

    /**
     * 根据查询条件获取APP列表
     *
     * @param appName 查询条件
     * @param catalog 类别
     * @param status  状态
     * @param page    当前页
     * @param rows    数量
     * @return APP列表
     */
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Object getAppList(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "appId", value = "appId", defaultValue = "")
                             @RequestParam(value = "appId") String appId,
                             @ApiParam(name = "appName", value = "appName", defaultValue = "")
                             @RequestParam(value = "appName") String appName,
                             @ApiParam(name = "catalog", value = "类别", defaultValue = "")
                             @RequestParam(value = "catalog") String catalog,
                             @ApiParam(name = "status", value = "状态", defaultValue = "")
                             @RequestParam(value = "status") String status,
                             @ApiParam(name = "page", value = "当前页", defaultValue = "")
                             @RequestParam(value = "page") int page,
                             @ApiParam(name = "rows", value = "页数", defaultValue = "")
                             @RequestParam(value = "rows") int rows) {

        Object object = appClient.getAppList(apiVersion, appId, appName, catalog, status, page, rows);
        return object;
    }

    /**
     * 删除APP信息
     *
     * @param appId APPId
     * @return 操作结果
     */
    @RequestMapping(value = "/id", method = RequestMethod.DELETE)
    public Object deleteApp(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "appId", value = "appid", defaultValue = "")
                            @RequestParam(value = "appId") String appId) {
        return appClient.deleteApp(apiVersion, appId);
    }

    /**
     * 获取APP明细
     *
     * @param appId APPId
     * @return APP明细
     */
    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public MAppDetail getAppDetail(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                               @PathVariable(value = "api_version") String apiVersion,
                               @ApiParam(name = "appId", value = "appid", defaultValue = "")
                               @RequestParam(value = "appId") String appId) throws Exception {

        Object object = appClient.getAppDetail(apiVersion, appId);

        ObjectMapper objectMapper = new ObjectMapper();
        String appJson = objectMapper.writeValueAsString(object);
        MAppDetail mAppDetail = objectMapper.readValue(appJson, MAppDetail.class);

        return mAppDetail;
    }

    /**
     * 创建APP信息
     *
     * @param name        名称
     * @param catalog     类别
     * @param url         URL地址
     * @param description 说明
     * @param tags        标记
     * @param userId      用户ID
     * @return 操作结果
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public MApp createApp(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
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
                            @ApiParam(name = "userId", value = "用户", defaultValue = "")
                            @RequestParam(value = "userId") String userId) throws Exception{

        Object object = appClient.createApp(apiVersion, name, catalog, url, description, tags, userId);

        ObjectMapper objectMapper = new ObjectMapper();
        String appJson = objectMapper.writeValueAsString(object);
        MApp mApp = objectMapper.readValue(appJson, MApp.class);

        return mApp;
    }

    /**
     * 修改APP信息
     *
     * @param appId       APPId
     * @param name        名称
     * @param catalog     类别
     * @param status      状态
     * @param url         URL地址
     * @param description 说明
     * @param tags        标记
     * @return 操作结果
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public MApp updateApp(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                            @PathVariable(value = "api_version") String apiVersion,
                            @ApiParam(name = "appId", value = "名id", defaultValue = "")
                            @RequestParam(value = "appId") String appId,
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
                            @RequestParam(value = "tags") String tags) throws Exception{

        Object object = appClient.updateApp(apiVersion, appId, name, catalog, status, url, description, tags);

        ObjectMapper objectMapper = new ObjectMapper();
        String appJson = objectMapper.writeValueAsString(object);
        MApp mApp = objectMapper.readValue(appJson, MApp.class);

        return mApp;
    }

    /**
     * 修改APP状态
     *
     * @param appId  APPID
     * @param status 状态
     * @return 操作结果
     */
    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    public Object checkStatus(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                        @PathVariable(value = "api_version") String apiVersion,
                        @ApiParam(name = "appId", value = "名id", defaultValue = "")
                        @RequestParam(value = "appId") String appId,
                        @ApiParam(name = "status", value = "状态", defaultValue = "")
                        @RequestParam(value = "status") String status) {
        Object object =appClient.checkStatus(apiVersion, appId, status);
        return object;
    }

    /**
     * 判断秘钥是否存在
     *
     * @param id     id
     * @param secret 秘钥
     * @return 操作结果
     * @throws Exception
     */
    @RequestMapping(value = "/validation", method = RequestMethod.GET)
    public Object validationApp(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "id", value = "名id", defaultValue = "")
                                @RequestParam(value = "id") String id,
                                @ApiParam(name = "secret", value = "状态", defaultValue = "")
                                @RequestParam(value = "secret") String secret) {

        return appClient.validationApp(apiVersion, id, secret);
    }

}
