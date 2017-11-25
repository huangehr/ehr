package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisMqPublisher;
import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import com.yihu.ehr.redis.pubsub.service.RedisMqPublisherService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Redis消息发布者 接口
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "消息发布者接口", tags = {"Redis消息发布订阅--消息发布者接口"})
public class RedisMqPublisherEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisMqPublisherService redisMqPublisherService;

    @ApiOperation("根据ID获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.GetById, method = RequestMethod.GET)
    public MRedisMqPublisher getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisMqPublisherService.getById(id), MRedisMqPublisher.class);
    }

    @ApiOperation(value = "根据条件获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Search, method = RequestMethod.GET)
    List<MRedisMqPublisher> search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RedisMqPublisher> redisMqPublishers = redisMqPublisherService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisMqPublisherService.getCount(filters), page, size);
        return (List<MRedisMqPublisher>) convertToModels(redisMqPublishers, new ArrayList<MRedisMqPublisher>(), MRedisMqPublisher.class, fields);
    }

    @ApiOperation("新增消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.POST)
    public MRedisMqPublisher add(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqPublisher newRedisMqPublisher = toEntity(entityJson, RedisMqPublisher.class);
        newRedisMqPublisher.setCreateTime(DateTimeUtil.iso8601DateTimeFormat(new Date()));
        newRedisMqPublisher = redisMqPublisherService.save(newRedisMqPublisher);
        return convertToModel(newRedisMqPublisher, MRedisMqPublisher.class);
    }

    @ApiOperation("更新消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.PUT)
    public MRedisMqPublisher update(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqPublisher updateRedisMqPublisher = toEntity(entityJson, RedisMqPublisher.class);
        updateRedisMqPublisher.setCreateTime(updateRedisMqPublisher.getCreateTime().replace(" ", "+"));
        updateRedisMqPublisher = redisMqPublisherService.save(updateRedisMqPublisher);
        return convertToModel(updateRedisMqPublisher, MRedisMqPublisher.class);
    }

    @ApiOperation("删除消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "消息发布者ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        redisMqPublisherService.delete(id);
    }

    @ApiOperation("验证指定队列中发布者应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.IsUniqueAppId, method = RequestMethod.GET)
    public boolean isUniqueAppId(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "appId", value = "发布者应用ID", required = true)
            @RequestParam(value = "appId") String appId) throws Exception {
        return redisMqPublisherService.isUniqueAppId(id, channel, appId);
    }

}
