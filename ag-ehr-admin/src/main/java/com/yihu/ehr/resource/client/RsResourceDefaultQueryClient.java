package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 固化视图过滤条件 client
 *
 * @author 张进军
 * @created 2017.9.8 15:42
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsResourceDefaultQueryClient {

    @ApiOperation("根据ID获取固化视图过滤条件")
    @RequestMapping(value = ServiceApi.Resources.QueryByResourceId, method = RequestMethod.GET)
    String getByResourceId(
            @ApiParam(name = "resourceId", value = "视图主键", required = true)
            @RequestParam(value = "resourceId") String resourceId);

}
