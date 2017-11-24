package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.redis.MRedisMqPublisher;
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
 * Redis消息发布者 Client
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
@FeignClient(name = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisMqPublisherClient {

    @ApiOperation("根据ID获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.GetById, method = RequestMethod.GET)
    public MRedisMqPublisher getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据条件获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Search, method = RequestMethod.GET)
    public ResponseEntity<List<MRedisMqPublisher>> search(
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

    @ApiOperation("新增消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.POST)
    public MRedisMqPublisher add(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson);

    @ApiOperation("更新消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.PUT)
    public MRedisMqPublisher update(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson);

    @ApiOperation("删除消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "消息发布者ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证指定消息队列中发布者应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.IsUniqueAppId, method = RequestMethod.GET)
    public boolean isUniqueAppId(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "appId", value = "发布者应用ID", required = true)
            @RequestParam(value = "appId") String appId);

}
