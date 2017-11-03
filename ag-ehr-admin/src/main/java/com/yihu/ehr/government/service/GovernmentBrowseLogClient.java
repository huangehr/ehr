package com.yihu.ehr.government.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface GovernmentBrowseLogClient {

    @RequestMapping(value = ServiceApi.Government.AddGovernmentBrowseLog, method = RequestMethod.POST)
    @ApiOperation(value = "新增浏览记录")
    GovernmentBrowseLog saveGovernmentBrowseLog(@RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Government.searchGovernmentBrowseLog, method = RequestMethod.GET)
    @ApiOperation(value = "浏览记录")
    List<String> getBrowseName(@RequestParam(value = "userId", required = false) String userId);
}
