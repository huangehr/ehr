package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisMqChannel;
import com.yihu.ehr.redis.pubsub.CustomMessageListenerAdapter;
import com.yihu.ehr.redis.pubsub.MessageCommonBiz;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.redis.pubsub.service.RedisMqPublisherService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Redis消息队列 接口
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "消息队列接口", tags = {"Redis消息发布订阅--消息队列接口"})
public class RedisMqChannelEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisMqChannelService redisMqChannelService;
    @Autowired
    private RedisMqMessageLogService redisMqMessageLogService;
    @Autowired
    private RedisMqSubscriberService redisMqSubscriberService;
    @Autowired
    private RedisMqPublisherService redisMqPublisherService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @ApiOperation("根据ID获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.GetById, method = RequestMethod.GET)
    public MRedisMqChannel getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisMqChannelService.getById(id), MRedisMqChannel.class);
    }

    @ApiOperation(value = "根据条件获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Search, method = RequestMethod.GET)
    List<MRedisMqChannel> search(
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
        List<RedisMqChannel> redisMqChannels = redisMqChannelService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisMqChannelService.getCount(filters), page, size);
        return (List<MRedisMqChannel>) convertToModels(redisMqChannels, new ArrayList<MRedisMqChannel>(), MRedisMqChannel.class, fields);
    }

    @ApiOperation("新增消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.POST)
    public MRedisMqChannel add(
            @ApiParam(name = "entityJson", value = "消息队列JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqChannel newRedisMqChannel = toEntity(entityJson, RedisMqChannel.class);
        newRedisMqChannel.setCreateTime(DateTimeUtil.iso8601DateTimeFormat(new Date()));
        newRedisMqChannel = redisMqChannelService.save(newRedisMqChannel);

        // 开启该订阅者的消息队列的消息监听
        String channel = newRedisMqChannel.getChannel();
        CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel);
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(channel));

        return convertToModel(newRedisMqChannel, MRedisMqChannel.class);
    }

    @ApiOperation("更新消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.PUT)
    public MRedisMqChannel update(
            @ApiParam(name = "entityJson", value = "消息队列JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisMqChannel updateRedisMqChannel = toEntity(entityJson, RedisMqChannel.class);
        updateRedisMqChannel.setCreateTime(updateRedisMqChannel.getCreateTime().replace(" ", "+"));
        updateRedisMqChannel = redisMqChannelService.save(updateRedisMqChannel);
        return convertToModel(updateRedisMqChannel, MRedisMqChannel.class);
    }

    @ApiOperation("删除消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        RedisMqChannel redisMqChannel = redisMqChannelService.getById(id);
        String channel = redisMqChannel.getChannel();

        List<RedisMqMessageLog> messageLogList = redisMqMessageLogService.findByChannelAndStatus(channel, "0");
        if (messageLogList.size() != 0) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该消息队列存在未消费消息，不能删除。");
            return envelop;
        }
        List<RedisMqSubscriber> subscriberList = redisMqSubscriberService.findByChannel(channel);
        if (subscriberList.size() != 0) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该消息队列存在订阅者，不能删除。");
            return envelop;
        }

        redisMqChannelService.delete(id);

        // 删除该消息队列的消息监听
        CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel);
        redisMessageListenerContainer.removeMessageListener(messageListener, new ChannelTopic(channel));

        envelop.setSuccessFlg(true);
        return envelop;
    }

    @ApiOperation("验证消息队列编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannel, method = RequestMethod.GET)
    public boolean isUniqueChannel(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel) throws Exception {
        return redisMqChannelService.isUniqueChannel(id, channel);
    }

    @ApiOperation("验证消息队列名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannelName, method = RequestMethod.GET)
    public boolean isUniqueChannelName(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channelName", value = "消息队列名称", required = true)
            @RequestParam(value = "channelName") String channelName) throws Exception {
        return redisMqChannelService.isUniqueChannelName(id, channelName);
    }

    @ApiOperation("发布消息")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.SendMessage, method = RequestMethod.POST)
    public Envelop sendMessage(
            @ApiParam(name = "publisherAppId", value = "发布者应用ID", required = true)
            @RequestParam(value = "publisherAppId") String publisherAppId,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestParam(value = "message") String message) throws Exception {
        Envelop envelop = new Envelop();

        // 判断消息队列是否注册
        RedisMqChannel redisMqChannel = redisMqChannelService.findByChannel(channel);
        if (redisMqChannel == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("消息队列 " + channel + " 还未注册，需要先注册才能往队列发布消息。");
            return envelop;
        }
        // 判断队列是否绑定发布者
        RedisMqPublisher redisMqPublisher = redisMqPublisherService.findByChannelAndAppId(channel, publisherAppId);
        if (redisMqPublisher == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("消息队列 " + channel + " 中没绑定过应用ID为 " + publisherAppId + " 的发布者，需要先绑定才能发布消息。");
            return envelop;
        }

        // 记录消息
        RedisMqMessageLog redisMqMessageLog = MessageCommonBiz.newMessageLog(channel, publisherAppId, message);
        redisMqMessageLogService.save(redisMqMessageLog);

        // 发布消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageLogId", redisMqMessageLog.getId());
        messageMap.put("messageContent", message);
        redisTemplate.convertAndSend(channel, toJson(messageMap));

        envelop.setSuccessFlg(true);
        return envelop;
    }

}
