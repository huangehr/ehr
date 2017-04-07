package com.yihu.ehr.portal.service.function;

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

    @RequestMapping(value =  ApiVersion.Version1_0 + ServiceApi.UserApp.UserAppList, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id获取App列表")
    List<MUserApp> getUserAppById(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId", required = true) String userId);


    @RequestMapping(value = ApiVersion.Version1_0 +  ServiceApi.UserApp.UserAppShow, method = RequestMethod.GET)
    @ApiOperation(value = "更新用户权限应用的云门户展示状态")
    MUserApp updateUserAppShowFlag(
            @ApiParam(name = "id", value = "用户APP关联ID")
            @RequestParam(value = "id", required = true) String id,
            @ApiParam(name = "flag", value = "要更新的展示状态", defaultValue = "1")
            @RequestParam(value = "flag", required = true) String flag);
}
