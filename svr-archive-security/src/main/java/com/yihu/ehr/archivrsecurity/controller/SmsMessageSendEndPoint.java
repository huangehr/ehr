package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageSend;
import com.yihu.ehr.archivrsecurity.service.SmsMessageSendService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.archivesecurity.MSmsMessageSend;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "SmsMessageSend", description = "短信发送情况", tags = {"短信发送情况"})
public class SmsMessageSendEndPoint extends EnvelopRestEndPoint {

    @Autowired
    SmsMessageSendService smsMessageSendService;

    @ApiOperation(value = "短信发送情况列表查询")
    @RequestMapping(value = "/messages_send_info", method = RequestMethod.GET)
    public Collection<MSmsMessageSend> searchMessagesSendInfo(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<SmsMessageSend> smsMessageSendList = smsMessageSendService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, smsMessageSendService.getCount(filters), page, size);

        return convertToModels(smsMessageSendList, new ArrayList<MSmsMessageSend>(smsMessageSendList.size()), MSmsMessageSend.class, fields);
    }

    @ApiOperation(value = "短信发送情况新增")
    @RequestMapping(value = "/messages_send_info", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MSmsMessageSend createMessagesSendInfo(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        SmsMessageSend smsMessageSend = toEntity(jsonData, SmsMessageSend.class);
        smsMessageSendService.save(smsMessageSend);
        return convertToModel(smsMessageSend, MSmsMessageSend.class, null);
    }


    @ApiOperation(value = "短信发送情况状态跟新")
    @RequestMapping(value = "/messages_send_info/{id}", method = RequestMethod.PUT)
    public boolean updateMessagesSendInfoStatus(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "status", value = "status")
            @RequestParam(value = "status") int status) throws Exception {
        return smsMessageSendService.updateStatus(id,status);
    }


}
