package com.yihu.ehr.archivrsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageReplyTemplate;
import com.yihu.ehr.archivrsecurity.service.MessageReplyTempService;
import com.yihu.ehr.model.archivesecurity.MSmsMessageReplyTemplate;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/7/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "",description = "短信回复模板管理")
public class MessageReplyTempEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MessageReplyTempService messageReplyTempService;

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReplyTempates,method = RequestMethod.POST)
    @ApiOperation("短信模板")
    public MSmsMessageReplyTemplate saveMessageReplyTemp(
            @ApiParam("jsonData")
            @RequestBody String jsonData) throws IOException {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        SmsMessageReplyTemplate messageTemp = objectMapper.readValue(jsonData,SmsMessageReplyTemplate.class);
        return convertToModel(messageReplyTempService.save(messageTemp),MSmsMessageReplyTemplate.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReplyTempates,method = RequestMethod.PUT)
    @ApiOperation("短信模板")
    public MSmsMessageReplyTemplate updateMessageReplyTemp(
            @ApiParam("jsonData")
            @RequestBody String jsonData) throws IOException {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        SmsMessageReplyTemplate messageTemp = objectMapper.readValue(jsonData,SmsMessageReplyTemplate.class);
        return convertToModel(messageReplyTempService.save(messageTemp),MSmsMessageReplyTemplate.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReplyTempates,method = RequestMethod.DELETE)
    public boolean deleteMessageReplyTemp(
            @ApiParam("messageReplyCode")
            @RequestParam(value = "messageReplyCode")String messageReplyCode)
    {
        messageReplyTempService.delteByMessageReplyCode(messageReplyCode);
        return true;
    }

    @ApiOperation("短信模板查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageReplyTempates,method = RequestMethod.GET)
    public Collection<MSmsMessageReplyTemplate> getMessageReplyTemp(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MSmsMessageReplyTemplate> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<SmsMessageReplyTemplate> replyTemplates = messageReplyTempService.getMessageReplyTemplates(sorts,reducePage(page),size);
            total = replyTemplates.getTotalElements();
            rsList = convertToModels(replyTemplates.getContent(),new ArrayList<>(replyTemplates.getNumber()),MSmsMessageReplyTemplate.class,fields);
        }
        else
        {
            List<SmsMessageReplyTemplate> replyTemplates = messageReplyTempService.search(fields,filters,sorts,page,size);
            total = messageReplyTempService.getCount(filters);
            rsList = convertToModels(replyTemplates,new ArrayList<>(replyTemplates.size()),MSmsMessageReplyTemplate.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return rsList;
    }
}
