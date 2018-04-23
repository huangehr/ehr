package com.yihu.ehr.basic.getui.controller;

import com.yihu.ehr.basic.getui.service.GeTuiClientService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author litaohong on 2018/4/20
 * @project ehr
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "APP消息推送用户和客户端映射关系控制器", tags = {"APP消息推送用户和客户端映射关系控制器"})
public class GeTuiClientEndpoint extends EnvelopRestEndPoint {
    @Autowired
    private GeTuiClientService geTuiClientService;

    @ApiOperation("根据userId获取clientId")
    @RequestMapping(value = ServiceApi.AppPushMessage.findClientId, method = RequestMethod.POST)
    public Envelop getClientIdByUserId(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(value = "userId") String userId) {
        String result = geTuiClientService.getClientIdByUserId(userId);
        return success(result);
    }


    @ApiOperation("根据userId获取clientId列表")
    @RequestMapping(value = ServiceApi.AppPushMessage.findClientIdList, method = RequestMethod.POST)
    public Envelop getListClientIdByUserId(
            @ApiParam(name = "userIds", value = "用户id", required = true)
            @RequestParam(value = "userIds") String userIds) {
        List<String> result = geTuiClientService.getListClientIdByUserId(userIds);
        return success(result);
    }


    @ApiOperation("根据userId更新clientId")
    @RequestMapping(value = ServiceApi.AppPushMessage.updateClientId, method = RequestMethod.POST)
    public Envelop updateClientIdByUserId(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "clientId", value = "clientId", required = true)
            @RequestParam(value = "clientId") String clientId
    ) {
        String result = geTuiClientService.updateClientIdByUserId(userId,clientId);
        return success(result);
    }


}
