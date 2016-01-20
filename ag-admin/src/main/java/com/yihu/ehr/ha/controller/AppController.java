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

    /**
     * 根据查询条件获取APP列表
     * @param searchNm 查询条件
     * @param catalog 类别
     * @param status 状态
     * @param page 当前页
     * @param rows 数量
     * @return APP列表
     */
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

    /**
     * 删除APP信息
     * @param appId APPId
     * @return 操作结果
     */
    @RequestMapping(value = "/app", method = RequestMethod.DELETE)
    public Object deleteApp(@ApiParam(name = "appId", value = "appid", defaultValue = "")
                            @RequestParam(value = "appId") String appId) {
        return appClient.deleteApp(appId);
    }

    /**
     * 获取APP明细
     * @param appId APPId
     * @return APP明细
     */
    @RequestMapping(value = "/appDetail", method = RequestMethod.GET)
    public Object getAppDetail(@ApiParam(name = "appId", value = "appid", defaultValue = "")
                               @RequestParam(value = "appId") String appId) {
        return appClient.getAppDetail(appId);
    }

    /**
     * 创建APP信息
     * @param name 名称
     * @param catalog 类别
     * @param url URL地址
     * @param description 说明
     * @param tags 标记
     * @param userId 用户ID
     * @return 操作结果
     */
    @RequestMapping(value = "/app" , method = RequestMethod.POST)
    public Object createApp(
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
            @RequestParam(value = "userId") String userId){
        return appClient.createApp(name,catalog,url,description,tags,userId);
    }

    /**
     * 修改APP信息
     * @param appId APPId
     * @param name 名称
     * @param catalog 类别
     * @param status 状态
     * @param url URL地址
     * @param description 说明
     * @param tags 标记
     * @return 操作结果
     */
    @RequestMapping(value = "/app" , method = RequestMethod.PUT)
    public Object updateApp(
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
            @RequestParam(value = "tags") String tags){
        return appClient.updateApp(appId,name,catalog,status,url,description,tags);
    }

    /**
     * 修改APP状态
     * @param appId APPID
     * @param status 状态
     * @return 操作结果
     */
    @RequestMapping(value = "/check" , method = RequestMethod.PUT)
    public Object check(
            @ApiParam(name = "appId", value = "名id", defaultValue = "")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status){
        return  appClient.check(appId,status);
    }

    /**
     * 判断秘钥是否存在
     * @param id id
     * @param secret 秘钥
     * @return 操作结果
     * @throws Exception
     */
    @RequestMapping(value = "/validation" , method = RequestMethod.GET)
    public Object validationApp(
            @ApiParam(name = "id", value = "名id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "secret", value = "状态", defaultValue = "")
            @RequestParam(value = "secret") String secret){

        return  appClient.validationApp(id,secret);
    }

}
