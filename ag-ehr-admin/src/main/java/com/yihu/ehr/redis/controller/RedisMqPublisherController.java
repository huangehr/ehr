package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.redis.MRedisMqPublisher;
import com.yihu.ehr.redis.client.RedisMqPublisherClient;
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
 * Redis消息发布者 Controller
 *
 * @author 张进军
 * @date 2017/11/20 09:35
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "消息发布者接口", tags = {"Redis消息发布订阅--消息发布者接口"})
public class RedisMqPublisherController extends BaseController {

    @Autowired
    private RedisMqPublisherClient redisMqPublisherClient;

    @ApiOperation("根据ID获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRedisMqPublisher mMRedisMqPublisher = redisMqPublisherClient.getById(id);
            envelop.setObj(mMRedisMqPublisher);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "根据条件获取消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Search, method = RequestMethod.GET)
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
            ResponseEntity<List<MRedisMqPublisher>> responseEntity = redisMqPublisherClient.search(fields, filters, sorts, page, size);
            List<MRedisMqPublisher> mMRedisMqPublisherList = responseEntity.getBody();
            envelop = getResult(mMRedisMqPublisherList, getTotalCount(responseEntity), page, size);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqPublisher newMRedisMqPublisher = redisMqPublisherClient.add(entityJson);
            envelop.setObj(newMRedisMqPublisher);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "消息发布者JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqPublisher newMRedisMqPublisher = redisMqPublisherClient.update(entityJson);
            envelop.setObj(newMRedisMqPublisher);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除消息发布者")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息发布者ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            redisMqPublisherClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证指定消息队列中发布者应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqPublisher.IsUniqueAppId, method = RequestMethod.GET)
    public Envelop isUniqueAppId(
            @ApiParam(name = "id", value = "消息发布者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel,
            @ApiParam(name = "appId", value = "发布者应用ID", required = true)
            @RequestParam(value = "appId") String appId) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = redisMqPublisherClient.isUniqueAppId(id, channel, appId);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息发布者应用ID已存在，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RedisMqPublisherController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
