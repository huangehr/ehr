package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.portal.model.MessageRemind;
import com.yihu.ehr.portal.service.MessageRemindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private MessageRemindService remindService;


    @RequestMapping(value = "/messageRemind/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询消息列表")
    public List<MMessageRemind> searchMessages(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,appId,content,createDate")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestBody(required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "+createDate,+readed")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<MessageRemind> messageList = remindService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, remindService.getCount(filters), page, size);
        return (List<MMessageRemind>) convertToModels(messageList, new ArrayList<MMessageRemind>(messageList.size()), MMessageRemind.class, fields);
    }


    @RequestMapping(value = "/messageRemind", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增-消息提醒信息")
    public MMessageRemind saveMessage(
            @ApiParam(name = "messageJsonData", value = "消息信息json数据")
            @RequestBody String messageJsonData
    ) throws Exception {
        MessageRemind remind = toEntity(messageJsonData, MessageRemind.class);
        remind.setReaded(0);
        remind.setCreateDate(new Date());
        remindService.save(remind);
        return convertToModel(remind, MMessageRemind.class);
    }

    @RequestMapping(value = "/messageRemind", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "阅读-消息信息")
    public MMessageRemind readMessage(
            @ApiParam(name = "messageId", value = "消息ID")
            @RequestParam(value = "messageId", required = true) int messageId
    ) throws Exception {
        MessageRemind remind = remindService.readMessage(messageId);
        return convertToModel(remind, MMessageRemind.class);
    }

}
