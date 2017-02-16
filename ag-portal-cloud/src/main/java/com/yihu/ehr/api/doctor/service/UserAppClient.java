package com.yihu.ehr.api.doctor.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MUserApp;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by linz on 2016年7月8日11:30:03.
 */
@FeignClient(name=MicroServices.Application)
public interface UserAppClient {

    @RequestMapping(value =  ApiVersion.Version1_0 +ServiceApi.UserApp.UserAppList, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id获取App列表")
    public List<MUserApp> getUserAppById(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId", required = true) String userId);
}
