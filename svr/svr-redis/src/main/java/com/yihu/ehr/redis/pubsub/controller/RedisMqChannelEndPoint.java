package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheCategory;
import com.yihu.ehr.model.redis.MRedisMqChannel;
import com.yihu.ehr.redis.pubsub.CustomMessageListenerAdapter;
import com.yihu.ehr.redis.pubsub.MessageCommonBiz;
import com.yihu.ehr.redis.pubsub.entity.RedisMqChannel;
import com.yihu.ehr.redis.pubsub.entity.RedisMqMessageLog;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqMessageLogService;
import com.yihu.ehr.redis.pubsub.service.RedisMqPublisherService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @ApiOperation("根据ID获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisMqChannel mRedisMqChannel = convertToModel(redisMqChannelService.getById(id), MRedisMqChannel.class);
            envelop.setObj(mRedisMqChannel);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息队列。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息队列发生异常：" + e.getMessage());
        }
        return envelop;
    }

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
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<RedisMqChannel> redisMqChannelList = redisMqChannelService.search(fields, filters, sorts, page, size);
            int count = (int) redisMqChannelService.getCount(filters);
            List<MRedisMqChannel> mRedisMqChannelList = (List<MRedisMqChannel>) convertToModels(redisMqChannelList, new ArrayList<MRedisMqChannel>(), MRedisMqChannel.class, fields);
            envelop = getPageResult(mRedisMqChannelList, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息队列列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息队列发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "消息队列JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqChannel newEntity = objectMapper.readValue(entityJson, RedisMqChannel.class);
            newEntity = redisMqChannelService.save(newEntity);

            // 开启该订阅者的消息队列的消息监听
            String channel = newEntity.getChannel();
            CustomMessageListenerAdapter messageListener = MessageCommonBiz.newCustomMessageListenerAdapter(channel);
            redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(channel));

            MRedisCacheCategory mRedisCacheCategory = convertToModel(newEntity, MRedisCacheCategory.class);
            envelop.setObj(mRedisCacheCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增消息队列。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增消息队列发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "消息队列JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqChannel updateEntity = objectMapper.readValue(entityJson, RedisMqChannel.class);
            updateEntity = redisMqChannelService.save(updateEntity);

            MRedisMqChannel mRedisMqChannel = convertToModel(updateEntity, MRedisMqChannel.class);
            envelop.setObj(mRedisMqChannel);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新消息队列。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新消息队列发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id) {
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
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisMqChannelService.isUniqueChannel(id, channel);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息队列编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证消息队列名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannelName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channelName", value = "消息队列名称", required = true)
            @RequestParam(value = "channelName") String channelName) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisMqChannelService.isUniqueChannelName(id, channelName);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息队列名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

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
