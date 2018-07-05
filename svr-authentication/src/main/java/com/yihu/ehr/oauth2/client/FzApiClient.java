package com.yihu.ehr.oauth2.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author 张进军
 * @date 2018/7/4 10:52
 */
@FeignClient(name = MicroServices.Basic)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface FzApiClient {

    @ApiOperation("转发福州总部内网接口")
    @RequestMapping(value = ServiceApi.Fz.InnerApi, method = RequestMethod.POST)
    Envelop fzInnerApi(
            @ApiParam(name = "api", value = "API 名称，格式为 a.b.c", required = true)
            @RequestParam(value = "api") String api,
            @ApiParam(name = "paramsJson", value = "参数JSON字符串", required = true)
            @RequestParam(value = "paramsJson") String paramsJson,
            @ApiParam(name = "apiVersion", value = "API 版本号，版本号为整型，从数字 1 开始递增", required = true)
            @RequestParam(value = "apiVersion") int apiVersion);

}
