package com.yihu.ehr.portal.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 *
 */
@FeignClient(name=MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalTjQuotaClient {


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaWarn, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标预警信息", notes = "获取指标预警信息")
    List<MTjQuotaWarn> getTjQuotaWarn(@RequestParam(value = "userId") String userId);

}
