package com.yihu.ehr.profile.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.Envelop;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 14:58
 */
@ApiIgnore
@FeignClient(value = MicroServices.Resource)
public interface XResourceClient {

    @RequestMapping(value = ApiVersion.Version1_0+ "/rs/query/getResources", method = GET)
    Envelop getResources(@RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                        @RequestParam(value = "appId", required = true) String appId,
                        @RequestParam(value = "queryParams", required = false) String queryParams) throws Exception;

    @RequestMapping(value = ApiVersion.Version1_0+ "/rs/query/getResources", method = GET)
    Envelop getResources(@RequestParam(value = "resourcesCode", required = true) String resourcesCode,
                                          @RequestParam(value = "appId", required = true) String appId,
                                          @RequestParam(value = "queryParams", required = false) String queryParams,
                                          @RequestParam(value = "page", required = false) Integer page,
                                          @RequestParam(value = "size", required = false) Integer size) throws Exception;

    //不通过资源代码查询细表数据
    @RequestMapping(value = ApiVersion.Version1_0+ "/rs/query/getEhrCenterSub", method = GET)
    Envelop getEhrCenterSub(@RequestParam(value = "queryParams", required = true) String queryParams,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "size", required = false) Integer size) throws Exception;
}
