package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.30
 */
@FeignClient(name = MicroServices.FzGateway)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface FzGatewayClient {

    @RequestMapping(value = ServiceApi.GateWay.FzGateway, method = RequestMethod.GET)
    @ApiOperation(value = "获取健康上饶的用户id")
    String getEhrUserId(@RequestParam String userId);
}
