package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageReply;
import com.yihu.ehr.archivrsecurity.service.SmsMessageReplyService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.archivesecurity.MSmsMessageReply;
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
@Api(value = "SmsMessageReply", description = "短信回复管理", tags = {"短信回复管理"})
public class SmsMessageReplyEndPoint extends EnvelopRestEndPoint {

    @Autowired
    SmsMessageReplyService smsMessageReplyService;

    @ApiOperation(value = "短信回复列表查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReply, method = RequestMethod.GET)
    public Collection<MSmsMessageReply> searchMessagesSendInfo(
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
        List<SmsMessageReply> smsMessageSendList = smsMessageReplyService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, smsMessageReplyService.getCount(filters), page, size);

        return convertToModels(smsMessageSendList, new ArrayList<MSmsMessageReply>(smsMessageSendList.size()), MSmsMessageReply.class, fields);
    }

    @ApiOperation(value = "短信回复新增")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReply, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MSmsMessageReply createMessagesSendInfo(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        SmsMessageReply smsMessageReply = toEntity(jsonData, SmsMessageReply.class);
        smsMessageReplyService.save(smsMessageReply);
        return convertToModel(smsMessageReply, MSmsMessageReply.class, null);
    }


}
