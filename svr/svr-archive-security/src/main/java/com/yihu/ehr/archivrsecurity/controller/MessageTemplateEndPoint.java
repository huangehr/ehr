package com.yihu.ehr.archivrsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageTemplate;
import com.yihu.ehr.archivrsecurity.service.MessageTemplateService;
import com.yihu.ehr.model.archivesecurity.MSmsMessageTemplate;
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
@Api(value = "message_template",description = "短信模板管理")
public class MessageTemplateEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MessageTemplateService messageTempService;

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageTempates, method = RequestMethod.POST)
    @ApiOperation("短信模板保存")
    public MSmsMessageTemplate saveMessageTemp(
            @ApiParam("jsonData")
            @RequestBody String jsonData) throws IOException
    {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        SmsMessageTemplate messageTemp = objectMapper.readValue(jsonData,SmsMessageTemplate.class);
        return convertToModel(messageTempService.save(messageTemp),MSmsMessageTemplate.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageTempates, method = RequestMethod.PUT)
    @ApiOperation("短信模板更新")
    public MSmsMessageTemplate updateMessageTemp(
            @ApiParam("jsonData")
            @RequestBody String jsonData) throws IOException
    {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        SmsMessageTemplate messageTemp = objectMapper.readValue(jsonData,SmsMessageTemplate.class);
        return convertToModel(messageTempService.save(messageTemp),MSmsMessageTemplate.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageTempatesCode, method = RequestMethod.DELETE)
    @ApiOperation("短信模板删除")
    public boolean deleteMessageTemp(
            @ApiParam("messageTempCode")
            @PathVariable(value = "messageTempCode")String messageTempCode) throws IOException
    {
        messageTempService.deleteByMessageTempCode(messageTempCode);
        return true;
    }

    @ApiOperation("短信模板查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.MessageTempates,method = RequestMethod.GET)
    public Collection<MSmsMessageTemplate> getMessageTemp(
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
        Collection<MSmsMessageTemplate> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<SmsMessageTemplate> messageTemplates = messageTempService.getMessageTemplates(sorts,reducePage(page),size);
            total = messageTemplates.getTotalElements();
            rsList = convertToModels(messageTemplates.getContent(),new ArrayList<>(messageTemplates.getNumber()),MSmsMessageTemplate.class,fields);
        }
        else
        {
            List<SmsMessageTemplate> messageTemplates = messageTempService.search(fields,filters,sorts,page,size);
            total = messageTempService.getCount(filters);
            rsList = convertToModels(messageTemplates,new ArrayList<>(messageTemplates.size()),MSmsMessageTemplate.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return rsList;
    }
}
