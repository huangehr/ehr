package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author hzp
 * @version 1.0
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Resource)
public interface XResourceClient {


    @RequestMapping(value = ServiceApi.Resources.ResourcesQuery, method = POST)
    Envelop getResources(
            @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
            @RequestParam(value = "appId", required = true) String appId,
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) throws Exception;

    //不通过资源代码查询细表数据
    @RequestMapping(value = "/rs/query/getEhrCenterSub", method = GET)
    Envelop getEhrCenterSub(@RequestParam(value = "queryParams", required = true) String queryParams,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "size", required = false) Integer size) throws Exception;

    @RequestMapping(value = "/rs/query/getRawFiles", method = GET)
    Envelop getRawFiles(@RequestParam(value = "profileId", required = false) String profileId,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "size", required = false) Integer size) throws Exception;
}
