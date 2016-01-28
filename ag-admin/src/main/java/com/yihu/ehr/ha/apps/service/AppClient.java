package com.yihu.ehr.ha.apps.service;

import com.yihu.ehr.model.app.MApp;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient("svr-app")
@RequestMapping("/rest/v1.0/app")
public interface AppClient {

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    Object getAppList(
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
                      @RequestParam(value = "rows") String rows);

    @RequestMapping(value = "/app", method = RequestMethod.DELETE)
    Object deleteApp(@RequestParam(value = "appId") String appId);

    @RequestMapping(value = "/app", method = RequestMethod.POST)
    MApp createApp(
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "catalog") String catalog,
                     @RequestParam(value = "url") String url,
                     @RequestParam(value = "description") String description,
                     @RequestParam(value = "tags") String tags,
                     @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/appDetail", method = RequestMethod.GET)
    Object getAppDetail(@RequestParam(value = "appId") String appId);

    @RequestMapping(value = "/app", method = RequestMethod.PUT)
    Object updateApp(@RequestParam(value = "appId") String appId,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "catalog") String catalog,
                     @RequestParam(value = "status") String status,
                     @RequestParam(value = "url") String url,
                     @RequestParam(value = "description") String description,
                     @RequestParam(value = "tags") String tags);

    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    Object check(@RequestParam(value = "appId") String appId,
                 @RequestParam(value = "status") String status);


    @RequestMapping(value = "validation", method = RequestMethod.GET)
    Object validationApp(@RequestParam(value = "id") String id,
                         @RequestParam(value = "secret") String secret);
}
