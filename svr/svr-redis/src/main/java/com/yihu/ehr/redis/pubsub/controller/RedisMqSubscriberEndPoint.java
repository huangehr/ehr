package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.redis.MRedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.DefaultMessageDelegate;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Redis消息订阅者 接口
 *
 * @author 张进军
 * @date 2017/11/13 15:14
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "消息订阅者接口", tags = {"Redis消息发布订阅--消息订阅者接口"})
public class RedisMqSubscriberEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;
    @Autowired
    private RedisMqChannelService redisMqChannelService;
    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @ApiOperation("根据ID获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.GetById, method = RequestMethod.GET)
    public MRedisMqSubscriber getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisMqSubscriberService.getById(id), MRedisMqSubscriber.class);
    }

    @ApiOperation(value = "根据条件获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Search, method = RequestMethod.GET)
    List<MRedisMqSubscriber> search(
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
        List<RedisMqSubscriber> redisMqSubscribers = redisMqSubscriberService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisMqSubscriberService.getCount(filters), page, size);
        return (List<MRedisMqSubscriber>) convertToModels(redisMqSubscribers, new ArrayList<MRedisMqSubscriber>(), MRedisMqSubscriber.class, fields);
    }

    @ApiOperation("新增消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.POST)
    public MRedisMqSubscriber add(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqSubscriber newRedisMqSubscriber = toEntity(entityJson, RedisMqSubscriber.class);
        newRedisMqSubscriber.setCreateTime(DateTimeUtil.iso8601DateTimeFormat(new Date()));
        newRedisMqSubscriber = redisMqSubscriberService.save(newRedisMqSubscriber);

        // 开启该订阅者的消息队列的消息监听
        RedisMqChannel channel = redisMqChannelService.findByChannel(newRedisMqSubscriber.getChannel());
        MessageListenerAdapter messageListener = this.getMessageListenerAdapter(newRedisMqSubscriber.getSubscribedUrl());
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(channel.getChannel()));

        return convertToModel(newRedisMqSubscriber, MRedisMqSubscriber.class);
    }

    @ApiOperation("更新消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.PUT)
    public MRedisMqSubscriber update(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqSubscriber updateRedisMqSubscriber = toEntity(entityJson, RedisMqSubscriber.class);
        updateRedisMqSubscriber = redisMqSubscriberService.save(updateRedisMqSubscriber);
        return convertToModel(updateRedisMqSubscriber, MRedisMqSubscriber.class);
    }

    @ApiOperation("删除消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        RedisMqSubscriber redisMqSubscriber = redisMqSubscriberService.getById(id);

        redisMqSubscriberService.delete(id);

        // 删除该订阅者对消息队列的消息监听
        RedisMqChannel channel = redisMqChannelService.findByChannel(redisMqSubscriber.getChannel());
        MessageListenerAdapter messageListener = this.getMessageListenerAdapter(redisMqSubscriber.getSubscribedUrl());
        redisMessageListenerContainer.removeMessageListener(messageListener, new ChannelTopic(channel.getChannel()));
    }

    @ApiOperation("验证消息订阅者服务地址是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.IsUniqueSubscribedUrl, method = RequestMethod.GET)
    public boolean isUniqueSubscribedUrl(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "subscriberUrl", value = "消息订阅者服务地址", required = true)
            @RequestParam(value = "subscriberUrl") String subscriberUrl) throws Exception {
        return redisMqSubscriberService.isUniqueSubscribedUrl(id, subscriberUrl);
    }

    // 获取消息队列监听器
    private MessageListenerAdapter getMessageListenerAdapter(String subscribedUrl) {
        DefaultMessageDelegate defaultMessageDelegate = new DefaultMessageDelegate(subscribedUrl);
        SpringContext.autowiredBean(defaultMessageDelegate);
        MessageListenerAdapter messageListener = new MessageListenerAdapter();
        messageListener.setDelegate(defaultMessageDelegate);
        SpringContext.autowiredBean(messageListener);
        return messageListener;
    }

}
