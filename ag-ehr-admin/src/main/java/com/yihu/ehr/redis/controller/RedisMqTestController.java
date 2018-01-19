package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张进军
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api("Redis 消息接收测试接口")
public class RedisMqTestController extends BaseController {

    @ApiOperation("接收消息测试")
    @RequestMapping(value = "/redis/mq/subscriber/receiveMessage", method = RequestMethod.POST)
    public void receiveMessage(
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) {
        System.out.println("receiveMessage 回调到的消息：" + message);
    }

    @ApiOperation("接收消息测试02")
    @RequestMapping(value = "/redis/mq/subscriber/receiveMessage02", method = RequestMethod.POST)
    public void receiveMessage02(
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) {
        System.out.println("receiveMessage02 回调到的消息：" + message);
    }

    @ApiOperation("接收消息测试03")
    @RequestMapping(value = "/redis/mq/subscriber/receiveMessage03", method = RequestMethod.POST)
    public void receiveMessage03(
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) {
        System.out.println("receiveMessage03 回调到的消息：" + message);
    }

}
