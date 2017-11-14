package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.redis.MRedisMqSubscriber;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Redis消息订阅者 Client
 *
 * @author 张进军
 * @date 2017/11/13 15:14
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisMqSubscriberClient {

    @ApiOperation("根据ID获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.GetById, method = RequestMethod.GET)
    public MRedisMqSubscriber getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据条件获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Search, method = RequestMethod.GET)
    public ResponseEntity<List<MRedisMqSubscriber>> search(
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

    @ApiOperation("新增消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.POST)
    public MRedisMqSubscriber add(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson);

    @ApiOperation("更新消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.PUT)
    public MRedisMqSubscriber update(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson);

    @ApiOperation("删除消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证消息订阅者服务地址是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.IsUniqueSubscribedUrl, method = RequestMethod.GET)
    public boolean isUniqueSubscribedUrl(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "subscriberUrl", value = "消息订阅者服务地址", required = true)
            @RequestParam(value = "subscriberUrl") String subscriberUrl);

}
