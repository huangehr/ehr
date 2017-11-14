package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.redis.MRedisMqSubscriber;
import com.yihu.ehr.redis.client.RedisMqSubscriberClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Redis消息订阅者 Controller
 *
 * @author 张进军
 * @date 2017/11/13 15:14
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api("Redis消息订阅者接口")
public class RedisMqSubscriberController extends BaseController {

    @Autowired
    private RedisMqSubscriberClient redisMqSubscriberClient;

    @ApiOperation("根据ID获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRedisMqSubscriber mMRedisMqSubscriber = redisMqSubscriberClient.getById(id);
            envelop.setObj(mMRedisMqSubscriber);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "根据条件获取消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRedisMqSubscriber>> responseEntity = redisMqSubscriberClient.search(fields, filters, sorts, page, size);
            List<MRedisMqSubscriber> mMRedisMqSubscriberList = responseEntity.getBody();
            envelop = getResult(mMRedisMqSubscriberList, getTotalCount(responseEntity), page, size);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqSubscriber newMRedisMqSubscriber = redisMqSubscriberClient.add(entityJson);
            envelop.setObj(newMRedisMqSubscriber);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "消息订阅者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqSubscriber newMRedisMqSubscriber = redisMqSubscriberClient.update(entityJson);
            envelop.setObj(newMRedisMqSubscriber);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除消息订阅者")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            redisMqSubscriberClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证消息订阅者服务地址是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqSubscriber.IsUniqueSubscribedUrl, method = RequestMethod.GET)
    public Envelop isUniqueSubscribedUrl(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "subscriberUrl", value = "消息订阅者服务地址", required = true)
            @RequestParam(value = "subscriberUrl") String subscriberUrl) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = redisMqSubscriberClient.isUniqueSubscribedUrl(id, subscriberUrl);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息订阅者服务地址已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqSubscriberController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
