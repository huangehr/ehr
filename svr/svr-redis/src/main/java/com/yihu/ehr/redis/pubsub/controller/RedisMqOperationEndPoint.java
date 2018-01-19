package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redis消息队列操作 接口
 *
 * @author 张进军
 * @date 2017/12/27 16:09
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "消息队列操作接口", tags = {"Redis消息发布订阅--消息队列操作接口"})
public class RedisMqOperationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisMqChannelService redisMqChannelService;

    @ApiOperation("发布消息")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.SendMessage, method = RequestMethod.POST)
    public Envelop sendMessage(
            @ApiParam(name = "publisherAppId", value = "发布者应用ID", required = true)
            @RequestParam(value = "publisherAppId") String publisherAppId,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestParam(value = "message") String message) {
        return redisMqChannelService.sendMessage(publisherAppId, channel, message);
    }

}
