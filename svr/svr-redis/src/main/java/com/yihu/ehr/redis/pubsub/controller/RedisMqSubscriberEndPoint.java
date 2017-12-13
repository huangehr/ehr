package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.entity.RedisMqSubscriber;
import com.yihu.ehr.redis.pubsub.service.RedisMqChannelService;
import com.yihu.ehr.redis.pubsub.service.RedisMqSubscriberService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisMqSubscriber mRedisMqSubscriber = convertToModel(redisMqSubscriberService.getById(id), MRedisMqSubscriber.class);
            envelop.setObj(mRedisMqSubscriber);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息订阅者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息订阅者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Search, method = RequestMethod.GET)
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
            List<RedisMqSubscriber> list = redisMqSubscriberService.search(fields, filters, sorts, page, size);
            int count = (int) redisMqSubscriberService.getCount(filters);
            List<MRedisMqSubscriber> mList = (List<MRedisMqSubscriber>) convertToModels(list, new ArrayList<MRedisMqSubscriber>(), MRedisMqSubscriber.class, fields);
            envelop = getPageResult(mList, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息订阅者列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息订阅者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "消息订阅者JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqSubscriber newEntity = objectMapper.readValue(entityJson, RedisMqSubscriber.class);
            newEntity = redisMqSubscriberService.save(newEntity);

            MRedisMqSubscriber mRedisMqSubscriber = convertToModel(newEntity, MRedisMqSubscriber.class);
            envelop.setObj(mRedisMqSubscriber);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增消息订阅者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增消息订阅者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "消息订阅者JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqSubscriber updateEntity = objectMapper.readValue(entityJson, RedisMqSubscriber.class);
            updateEntity = redisMqSubscriberService.save(updateEntity);

            MRedisMqSubscriber mRedisMqSubscriber = convertToModel(updateEntity, MRedisMqSubscriber.class);
            envelop.setObj(mRedisMqSubscriber);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新消息订阅者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新消息订阅者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            redisMqSubscriberService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除消息订阅者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除消息订阅者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证指定消息队列中订阅者应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.IsUniqueAppId, method = RequestMethod.GET)
    public Envelop isUniqueAppId(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "appId", value = "消息订阅者应用ID", required = true)
            @RequestParam(value = "appId") String appId) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisMqSubscriberService.isUniqueAppId(id, channel, appId);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该指定消息队列中订阅者应用ID已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证指定消息队列中订阅者服务地址是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.IsUniqueSubscribedUrl, method = RequestMethod.GET)
    public Envelop isUniqueSubscribedUrl(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "subscriberUrl", value = "消息订阅者服务地址", required = true)
            @RequestParam(value = "subscriberUrl") String subscriberUrl) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisMqSubscriberService.isUniqueSubscribedUrl(id, channel, subscriberUrl);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该指定消息队列中订阅者服务地址已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
