package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Redis消息队列 Client
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisMqChannelClient {

    @ApiOperation("根据ID获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据条件获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("新增消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "消息队列JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("更新消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "消息队列JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("删除消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证消息队列编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannel, method = RequestMethod.GET)
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel);

    @ApiOperation("验证消息队列名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannelName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channelName", value = "消息队列名称", required = true)
            @RequestParam(value = "channelName") String channelName);

}
