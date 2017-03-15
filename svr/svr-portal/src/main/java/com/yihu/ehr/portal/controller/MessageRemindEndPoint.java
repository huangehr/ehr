package com.yihu.ehr.portal.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.portal.model.MessageRemind;
import com.yihu.ehr.portal.model.MessageRemind;
import com.yihu.ehr.portal.service.MessageRemindService;
import com.yihu.ehr.portal.service.MessageRemindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "messageRemind", description = "消息提醒管理服务", tags = {"消息管理"})
public class MessageRemindEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private MessageRemindService messageRemindService;

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public List<MMessageRemind> getMessageRemindTop10(){
        List<MessageRemind> list = messageRemindService.getMessageRemindTop10();
        return (List<MMessageRemind>) convertToModels(list, new ArrayList<MMessageRemind>(list.size()), MMessageRemind.class, null);
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息列表", notes = "根据查询条件获取提醒消息列表在前端表格展示")
    public List<MMessageRemind> searchMessageRemind(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<MessageRemind> messageRemindList = messageRemindService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, messageRemindService.getCount(filters), page, size);

        return (List<MMessageRemind>) convertToModels(messageRemindList, new ArrayList<MMessageRemind>(messageRemindList.size()), MMessageRemind.class, fields);
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建提醒消息", notes = "创建提醒消息信息")
    public MMessageRemind createMessageRemind(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestBody String messageRemindJsonData) throws Exception {
        MessageRemind messageRemind = toEntity(messageRemindJsonData, MessageRemind.class);
        messageRemind.setCreateDate(new Date());
        messageRemindService.save(messageRemind);
        return convertToModel(messageRemind, MMessageRemind.class);
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改提醒消息", notes = "重新绑定提醒消息信息")
    public MMessageRemind updateDoctor(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestBody String messageRemindJsonData) throws Exception {
        MessageRemind messageRemind = toEntity(messageRemindJsonData, MessageRemind.class);
        messageRemindService.save(messageRemind);
        return convertToModel(messageRemind, MMessageRemind.class);
    }


    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取提醒消息信息")
    public MMessageRemind getMessageRemind(
            @ApiParam(name = "messageRemind_id", value = "", defaultValue = "")
            @PathVariable(value = "messageRemind_id") Long messageRemindId) {
        MessageRemind messageRemind = messageRemindService.getMessageRemind(messageRemindId);
        MMessageRemind messageRemindModel = convertToModel(messageRemind, MMessageRemind.class);
        return messageRemindModel;
    }


    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除提醒消息", notes = "根据id删除提醒消息")
    public boolean deleteMessageRemind(
            @ApiParam(name = "messageRemind_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "messageRemind_id") Long messageRemindId) throws Exception {
        messageRemindService.deleteMessageRemind(messageRemindId);
        return true;
    }

//    @RequestMapping(value = "/messageRemind", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation(value = "阅读-消息信息")
//    public MMessageRemind readMessage(
//            @ApiParam(name = "messageId", value = "消息ID")
//            @RequestParam(value = "messageId", required = true) int messageId
//    ) throws Exception {
////        MessageRemind remind = remindService.readMessage(messageId);
////        return convertToModel(remind, MMessageRemind.class);
//    }

}
