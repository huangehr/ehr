package com.yihu.ehr.basic.getui;

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
 * @author litaohong on 2018/4/18
 * @project ehr
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "APP消息推送", tags = {"APP消息推送接口"})
public class AppPushMessageEndpoint extends EnvelopRestEndPoint {

    @Autowired
    private AppPushMessageService appPushMessageService;

    @ApiOperation("对单个用户推送消息")
    @RequestMapping(value = ServiceApi.AppPushMessage.single, method = RequestMethod.POST)
    public Envelop pushSingle(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "title", value = "消息标题", required = true)
            @RequestParam(value = "title") String title,
            @ApiParam(name = "message", value = "消息内容", required = true)
            @RequestParam(value = "message") String message,
            @ApiParam(name = "targetUrl", value = "点击阅读消息后跳转的url", required = true)
            @RequestParam(value = "targetUrl") String targetUrl) {
        String result = appPushMessageService.pushMessageToSingle(userId, title, message, targetUrl);
        return success(result);

    }

    @ApiOperation("对用户组推送消息")
    @RequestMapping(value = ServiceApi.AppPushMessage.list, method = RequestMethod.POST)
    public Envelop pushList(
            @ApiParam(name = "userIds", value = "用户id列表", required = true)
            @RequestParam(value = "userIds") List<String> userIds,
            @ApiParam(name = "title", value = "消息标题", required = true)
            @RequestParam(value = "title") String title,
            @ApiParam(name = "message", value = "消息内容", required = true)
            @RequestParam(value = "message") String message) {
        String result = appPushMessageService.pushMessageToList(userIds, title, message);
        return success(result);
    }

    @ApiOperation("对应用群推送消息")
    @RequestMapping(value = ServiceApi.AppPushMessage.app, method = RequestMethod.POST)
    public Envelop pushAPP(
            @ApiParam(name = "appIdList", value = "应用id列表", required = true)
            @RequestParam(value = "appIdList") List<String> appIdList,
            @ApiParam(name = "phoneList", value = "用户手机列表", required = false)
            @RequestParam(value = "phoneList") List<String> phoneList,
            @ApiParam(name = "cityList", value = "省，市编码列表", required = false)
            @RequestParam(value = "cityList") List<String> cityList,
            @ApiParam(name = "tagList", value = "标签名列表", required = false)
            @RequestParam(value = "tagList") List<String> tagList,
            @ApiParam(name = "title", value = "消息标题", required = true)
            @RequestParam(value = "title") String title,
            @ApiParam(name = "message", value = "消息内容", required = true)
            @RequestParam(value = "message") String message) {
        String result = appPushMessageService.pushMessageToAPP(appIdList, phoneList, cityList,tagList, title,message);
        return success(result);
    }

    @ApiOperation("透传消息")
    @RequestMapping(value = ServiceApi.AppPushMessage.tarns, method = RequestMethod.POST)
    public Envelop pushAPPTransimssion(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "title", value = "消息标题", required = true)
            @RequestParam(value = "title") String title,
            @ApiParam(name = "message", value = "消息内容", required = true)
            @RequestParam(value = "message") String message) {
        String result = appPushMessageService.pushMessageTransimssion(userId, title, message);
        return success(result);
    }
}
