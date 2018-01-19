package com.yihu.ehr.redis.pubsub.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisMqPublisher;
import com.yihu.ehr.redis.pubsub.entity.RedisMqPublisher;
import com.yihu.ehr.redis.pubsub.service.RedisMqPublisherService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisMqPublisher mRedisMqPublisher = convertToModel(redisMqPublisherService.getById(id), MRedisMqPublisher.class);
            envelop.setObj(mRedisMqPublisher);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息发布者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息发布者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Search, method = RequestMethod.GET)
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
            List<RedisMqPublisher> list = redisMqPublisherService.search(fields, filters, sorts, page, size);
            int count = (int) redisMqPublisherService.getCount(filters);
            List<MRedisMqPublisher> mList = (List<MRedisMqPublisher>) convertToModels(list, new ArrayList<MRedisMqPublisher>(), MRedisMqPublisher.class, fields);
            envelop = getPageResult(mList, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取消息发布者列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取消息发布者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "消息发布者JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqPublisher newEntity = objectMapper.readValue(entityJson, RedisMqPublisher.class);
            newEntity = redisMqPublisherService.save(newEntity);

            MRedisMqPublisher mRedisMqPublisher = convertToModel(newEntity, MRedisMqPublisher.class);
            envelop.setObj(mRedisMqPublisher);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增消息发布者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增消息发布者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "消息发布者JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisMqPublisher updateEntity = objectMapper.readValue(entityJson, RedisMqPublisher.class);
            updateEntity = redisMqPublisherService.save(updateEntity);

            MRedisMqPublisher mRedisMqPublisher = convertToModel(updateEntity, MRedisMqPublisher.class);
            envelop.setObj(mRedisMqPublisher);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新消息发布者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新消息发布者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息发布者ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            redisMqPublisherService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除消息发布者。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除消息发布者发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证指定队列中发布者应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.IsUniqueAppId, method = RequestMethod.GET)
    public Envelop isUniqueAppId(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "appId", value = "发布者应用ID", required = true)
            @RequestParam(value = "appId") String appId) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisMqPublisherService.isUniqueAppId(id, channel, appId);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该指定队列的发布者应用ID已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
