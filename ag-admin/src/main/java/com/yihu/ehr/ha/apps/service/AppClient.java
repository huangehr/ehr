package com.yihu.ehr.ha.apps.service;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient("svr-app")
public interface AppClient {

    @RequestMapping(value = "/rest/{api_version}/app/search", method = RequestMethod.GET)
    Object getAppList(@PathVariable(value = "api_version") String apiVersion,
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
                      @RequestParam(value = "rows") int rows);

    @RequestMapping(value = "/rest/{api_version}/app/", method = RequestMethod.DELETE)
    Object deleteApp(@PathVariable(value = "api_version") String apiVersion,
                     @RequestParam(value = "appId") String appId);

    @RequestMapping(value = "/rest/{api_version}/app/", method = RequestMethod.POST)
    Object createApp(@PathVariable(value = "api_version") String apiVersion,
                   @RequestParam(value = "name") String name,
                   @RequestParam(value = "catalog") String catalog,
                   @RequestParam(value = "url") String url,
                   @RequestParam(value = "description") String description,
                   @RequestParam(value = "tags") String tags,
                   @RequestParam(value = "userId") String userId);

    @RequestMapping(value = "/rest/{api_version}/app/detail", method = RequestMethod.GET)
    Object getAppDetail(@PathVariable(value = "api_version") String apiVersion,
                        @RequestParam(value = "appId") String appId);

    @RequestMapping(value = "/rest/{api_version}/app/", method = RequestMethod.PUT)
    Object updateApp(@PathVariable(value = "api_version") String apiVersion,
                     @RequestParam(value = "appId") String appId,
                     @RequestParam(value = "name") String name,
                     @RequestParam(value = "catalog") String catalog,
                     @RequestParam(value = "status") String status,
                     @RequestParam(value = "url") String url,
                     @RequestParam(value = "description") String description,
                     @RequestParam(value = "tags") String tags);

    @RequestMapping(value = "/rest/{api_version}/app/check", method = RequestMethod.PUT)
    Object checkStatus(@PathVariable(value = "api_version") String apiVersion,
                 @RequestParam(value = "appId") String appId,
                 @RequestParam(value = "status") String status);


    @RequestMapping(value = "/rest/{api_version}/app/validation", method = RequestMethod.GET)
    Object validationApp(@PathVariable(value = "api_version") String apiVersion,
                         @RequestParam(value = "id") String id,
                         @RequestParam(value = "secret") String secret);
}
