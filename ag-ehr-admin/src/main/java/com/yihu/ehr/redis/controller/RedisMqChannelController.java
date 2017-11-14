package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.redis.MRedisMqChannel;
import com.yihu.ehr.redis.client.RedisMqChannelClient;
import com.yihu.ehr.resource.controller.RsReportController;
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
 * Redis消息队列 Controller
 *
 * @author 张进军
 * @date 2017/11/10 11:45
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api("Redis消息队列接口")
public class RedisMqChannelController extends BaseController {

    @Autowired
    private RedisMqChannelClient redisMqChannelClient;

    @ApiOperation("根据ID获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            MRedisMqChannel mRedisMqChannel = redisMqChannelClient.getById(id);
            envelop.setObj(mRedisMqChannel);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation(value = "根据条件获取消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Search, method = RequestMethod.GET)
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
            ResponseEntity<List<MRedisMqChannel>> responseEntity = redisMqChannelClient.search(fields, filters, sorts, page, size);
            List<MRedisMqChannel> mRedisMqChannelList = responseEntity.getBody();
            envelop = getResult(mRedisMqChannelList, getTotalCount(responseEntity), page, size);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("新增消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "消息队列JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqChannel newMRedisMqChannel = redisMqChannelClient.add(entityJson);
            envelop.setObj(newMRedisMqChannel);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("更新消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "消息队列JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        Envelop envelop = new Envelop();
        try {
            MRedisMqChannel newMRedisMqChannel = redisMqChannelClient.update(entityJson);
            envelop.setObj(newMRedisMqChannel);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除消息队列")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            redisMqChannelClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证消息队列编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannel, method = RequestMethod.GET)
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channel", value = "消息队列编码", required = true)
            @RequestParam(value = "channel") String channel) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = redisMqChannelClient.isUniqueChannel(id, channel);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息队列编码已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("验证消息队列名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.MqChannel.IsUniqueChannelName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "消息队列ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "channelName", value = "消息队列名称", required = true)
            @RequestParam(value = "channelName") String channelName) throws Exception {
        Envelop envelop = new Envelop();
        try {
            boolean result = redisMqChannelClient.isUniqueChannel(id, channelName);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该消息队列名称已被使用，请重新填写！");
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(RsReportController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
