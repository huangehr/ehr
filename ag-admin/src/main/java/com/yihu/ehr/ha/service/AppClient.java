package com.yihu.ehr.ha.service;

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
    Object getAppList(@RequestParam(value = "searchNm") Integer searchNm,
                      @RequestParam(value = "catalog") Integer catalog,
                      @RequestParam(value = "status") Integer status,
                      @RequestParam(value = "page") Integer page,
                      @RequestParam(value = "rows") Integer rows);

    @RequestMapping(value = "/app", method = RequestMethod.DELETE)
    Object deleteApp(@RequestParam(value = "appId") String appId);

    @RequestMapping(value = "/app", method = RequestMethod.POST)
    Object createApp(@RequestParam(value = "name") String name,
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
