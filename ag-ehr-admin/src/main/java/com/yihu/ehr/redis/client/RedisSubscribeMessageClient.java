package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 接收消息队列的消息订阅接口 Client
 *
 * @author 张进军
 * @date 2017/12/4 17:07
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisSubscribeMessageClient {

    @ApiOperation("记录缓存获取接口的响应时间")
    @RequestMapping(value = ServiceApi.Redis.SubscribeMessage.ReceiveResponseTime, method = RequestMethod.POST)
    public void receiveResponseTime(
            @ApiParam(value = "消息", required = true)
            @RequestBody String message);

}
