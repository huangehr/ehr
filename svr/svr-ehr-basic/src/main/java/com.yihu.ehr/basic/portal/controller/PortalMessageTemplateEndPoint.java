package com.yihu.ehr.basic.portal.controller;

import com.alibaba.fastjson.JSON;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.basic.portal.service.PortalMessageTemplateService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.portal.MFzH5Message;
import com.yihu.ehr.model.portal.MMessageTemplate;
import com.yihu.ehr.model.portal.MMyMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PortalMessageTemplate", description = "消息模板管理", tags = {"云门户-消息模板管理"})
public class PortalMessageTemplateEndPoint extends EnvelopRestEndPoint {

    private PortalMessageTemplateService messageTemplateService;

    private PortalMessageRemindService messageRemindService;

    /**
     * 秘钥
     */
    @Value("${h5.secret}")
    public String secretKey;
    /**
     * 渠道ID
     */
    @Value("${h5.appId}")
    public String clientId;

    @Autowired
    public PortalMessageTemplateEndPoint(PortalMessageTemplateService messageTemplateService,
                                         PortalMessageRemindService messageRemindService) {
        this.messageTemplateService = messageTemplateService;
        this.messageRemindService = messageRemindService;
    }

    @RequestMapping(value = ServiceApi.MessageTemplate.MessageTemplate, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息模板列表", notes = "根据查询条件获取获取消息模板列表在前端表格展示")
    public List<MMessageTemplate> searchPortalMessageTemplate(
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
        List<PortalMessageTemplate> portalMessageTemplateList = messageTemplateService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, messageTemplateService.getCount(filters), page, size);

        return (List<MMessageTemplate>) convertToModels(portalMessageTemplateList, new ArrayList<MMessageTemplate>(portalMessageTemplateList.size()), MMessageTemplate.class, fields);
    }

    @RequestMapping(value = ServiceApi.MessageTemplate.MessageTemplateAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息模板对象", notes = "获取消息模板对象")
    public MMessageTemplate getMessageTemplateInfo(
            @ApiParam(name = "messageTemplateId", value = "模板id", defaultValue = "")
            @PathVariable(value = "messageTemplateId") Long messageTemplateId) {
        PortalMessageTemplate messageTemplate = messageTemplateService.getMessageTemplate(messageTemplateId);
        MMessageTemplate messageTemplateModel = convertToModel(messageTemplate, MMessageTemplate.class);
        return messageTemplateModel;
    }


    @RequestMapping(value = ServiceApi.MessageTemplate.MessageTemplate, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建消息模板对象", notes = "创建消息模板对象")
    public MMessageTemplate createMessageTemplate(
            @ApiParam(name = "messageTemplateJsonData", value = "", defaultValue = "")
            @RequestBody String messageTemplateJsonData) throws Exception {
        PortalMessageTemplate messageTemplate = toEntity(messageTemplateJsonData, PortalMessageTemplate.class);
        messageTemplateService.save(messageTemplate);
        return convertToModel(messageTemplate, MMessageTemplate.class);
    }

    @RequestMapping(value = ServiceApi.MessageTemplate.MessageTemplate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改消息模板对象", notes = "修改消息模板对象")
    public PortalMessageTemplate updateMessageTemplate(
            @ApiParam(name = "messageTemplateJsonData", value = "", defaultValue = "")
            @RequestBody String messageTemplateJsonData) throws Exception {
        PortalMessageTemplate messageTemplate = toEntity(messageTemplateJsonData, PortalMessageTemplate.class);
        messageTemplateService.save(messageTemplate);
        return convertToModel(messageTemplate, PortalMessageTemplate.class);
    }


    @RequestMapping(value = ServiceApi.MessageTemplate.MessageTemplateAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除消息模板对象", notes = "根据id删除消息模板")
    public boolean deletePortalMessageTemplate(
            @ApiParam(name = "messageTemplateId", value = "模板id", defaultValue = "")
            @PathVariable(value = "messageTemplateId") Long messageTemplateId) throws Exception {
        messageTemplateService.deletePortalMessageTemplate(messageTemplateId);
        return true;
    }


    @RequestMapping(value = ServiceApi.MessageTemplate.MyMessageList, method = RequestMethod.GET)
    @ApiOperation(value = "获取我的消息列表", notes = "获取我的消息列表")
    public List<MMyMessage> searchMyMessageList(
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
            HttpServletResponse response) throws ParseException, IOException {
        List<MMyMessage> mMyMessageList = new ArrayList<>();
        List<ProtalMessageRemind> messageRemindList = messageRemindService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, messageRemindService.getCount(filters), page, size);
        for (ProtalMessageRemind protalMessageRemind : messageRemindList) {
            MMyMessage mMyMessage = convertToMMyMessage(protalMessageRemind);
            mMyMessageList.add(mMyMessage);
        }
        return mMyMessageList;
    }

    @RequestMapping(value = ServiceApi.MessageTemplate.MyMessage, method = RequestMethod.GET)
    @ApiOperation(value = "获取我的消息对象", notes = "获取我的消息对象")
    public MMyMessage myMessage(
            @ApiParam(name = "messageId", value = "消息id", defaultValue = "")
            @PathVariable(value = "messageId") Long messageId) throws IOException {
        ProtalMessageRemind protalMessageRemind = messageRemindService.getMessageRemind(messageId);
        return convertToMMyMessage(protalMessageRemind);
    }


    /**
     * 数据转换
     *
     * @param protalMessageRemind 消息提醒对象
     * @return
     */
    private MMyMessage convertToMMyMessage(ProtalMessageRemind protalMessageRemind) {
        if (protalMessageRemind.getMessageTemplateId() == null) {
            throw new ApiException("模板ID不存在");
        }
        PortalMessageTemplate template = messageTemplateService.getMessageTemplate(protalMessageRemind.getMessageTemplateId());
        if (template == null) {
            throw new ApiException("模板对象不存在");
        }
        MMyMessage mMyMessage = convertToModel(protalMessageRemind, MMyMessage.class);
        mMyMessage.setTitle(template.getTitle());
        mMyMessage.setBeforeContent(template.getBeforeContent());
        mMyMessage.setAfterContent(template.getAfterContent());
        mMyMessage.setContentJsons(JSON.parseArray(protalMessageRemind.getContent(), MMyMessage.ContentJson.class));
        mMyMessage.setClassification(template.getClassification());
        return mMyMessage;
    }


    @RequestMapping(value = ServiceApi.MessageTemplate.MessageOrderPush, method = RequestMethod.POST)
    @ApiOperation(value = "接收H5挂号订单消息推送", notes = "接收H5挂号订单消息推送")
    public boolean messageOrderPush(@ApiParam(name = "jsonData", value = "", defaultValue = "")
                                    @RequestBody String jsonData) throws Exception {
        logger.info(String.format("收到H5挂号订单消息推送%s", jsonData));
        Map<String, String> map = toEntity(jsonData, Map.class);
        MFzH5Message message = toEntity(map.get("Param"), MFzH5Message.class);
        long messageTemplateId = 1;
        if (message.getOrderPushType() == 101) {
            //挂号推送
            messageTemplateId = 1;
        } else if (message.getOrderPushType() == 102) {
            //退号结果推送,目前先不推送
            messageTemplateId = 2;
            return true;
        }
        messageTemplateService.saveH5MessagePush(message, messageTemplateId);

        return true;
    }


}
