package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.redis.client.RedisSubscribeMessageClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张进军
 * @date 2017/12/4 17:07
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "接收消息队列的消息订阅接口", tags = {"缓存服务管理--接收消息队列的消息订阅接口"})
public class RedisSubscribeMessageController extends BaseController {

    @Autowired
    private RedisSubscribeMessageClient redisSubscribeMessageClient;

    @ApiOperation("记录缓存获取接口的响应时间")
    @RequestMapping(value = ServiceApi.Redis.SubscribeMessage.ReceiveResponseTime, method = RequestMethod.POST)
    public void receiveResponseTime(
            @ApiParam(value = "消息", required = true)
            @RequestBody String message) {
        redisSubscribeMessageClient.receiveResponseTime(message);
    }

}
