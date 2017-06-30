package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.portal.MPortalNotice;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
public interface TjQuotaClient {


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaWarn, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标预警信息", notes = "获取指标预警信息")
    List<MTjQuotaWarn> getTjQuotaWarn(@RequestParam(value = "userId") String userId);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaByCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据Code获取指标")
    public MTjQuotaModel getByCode(@RequestParam(value = "code") String code) ;


}
