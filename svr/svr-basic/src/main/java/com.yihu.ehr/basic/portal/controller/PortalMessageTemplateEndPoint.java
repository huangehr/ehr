package com.yihu.ehr.basic.portal.controller;

import com.alibaba.fastjson.JSON;
import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.basic.appointment.service.RegistrationService;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.model.portal.MProtalOrderMessage;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.basic.portal.service.PortalMessageTemplateService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.portal.MH5Message;
import com.yihu.ehr.model.portal.MMessageTemplate;
import com.yihu.ehr.model.portal.MMyMessage;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.id.UuidUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PortalMessageTemplate", description = "消息模板管理", tags = {"云门户-消息模板管理"})
public class PortalMessageTemplateEndPoint extends EnvelopRestEndPoint {
    private static final Logger LOG = LoggerFactory.getLogger(PortalMessageTemplateEndPoint.class);
    private PortalMessageTemplateService messageTemplateService;

    private PortalMessageRemindService messageRemindService;

    @Autowired
    private OpenService openService;
    @Autowired
    private RegistrationService registrationService;

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
    public Envelop searchMyMessageList(
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
        Envelop envelop = new Envelop();
        List<MMyMessage> mMyMessageList = new ArrayList<>();
        if(StringUtils.isEmpty(sorts)){
            sorts="-createDate";
        }
        List<ProtalMessageRemind> messageRemindList = messageRemindService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, messageRemindService.getCount(filters), page, size);
        for (ProtalMessageRemind protalMessageRemind : messageRemindList) {
            MMyMessage mMyMessage = convertToMMyMessage(protalMessageRemind);
            mMyMessageList.add(mMyMessage);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(mMyMessageList);
        envelop.setPageSize(size);
        envelop.setCurrPage(page);
        envelop.setTotalCount((int)messageRemindService.getCount(filters));
        return envelop;
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
    public String messageOrderPush(
            @ApiParam(name = "sign", value = "根据分配给第三方的秘钥对参数进行的签名",defaultValue = "2DD189BF620529952A21D565ADDBE6880084E413")
            @RequestParam(value = "sign", required = false) String sign,
            @ApiParam(name = "timestamp", value = "时间戳",defaultValue = "1524139460051")
            @RequestParam(value = "timestamp", required = false) Long timestamp,
            @ApiParam(name = "appId", value = "渠道ID",defaultValue = "9000401")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "agencyAbb", value = "定值yihuwang",defaultValue = "yihuwang")
            @RequestParam(value = "agencyAbb", required = false) String agencyAbb,
            @ApiParam(name = "orderId", value = "健康之路订单号",defaultValue = "60790942665210")
            @RequestParam(value = "orderId", required = false) String orderId,
            @ApiParam(name = "type", value = "推送类型",defaultValue = "101")
            @RequestParam(value = "type", required = false) Integer type,
            @ApiParam(name = "isSuccess", value = "是否成功",defaultValue = "0")
            @RequestParam(value = "isSuccess", required = false) Integer isSuccess,
            @ApiParam(name = "thirdPartyOrderId", value = "第三方订单ID",defaultValue = "yihuwang")
            @RequestParam(value = "thirdPartyOrderId", required = false) String thirdPartyOrderId,
            @ApiParam(name = "thirdPartyUserId", value = "第三方用户ID",defaultValue = "0dae00035ab8be56319e6d2e0f183443")
            @RequestParam(value = "thirdPartyUserId", required = false) String thirdPartyUserId,
            @ApiParam(name = "data", value = "消息string(json)")
            @RequestParam(value = "data", required = false) String data
            ) throws Exception {
        LOG.info(String.format("收到H5挂号订单消息推送-%s", data));
        Map<String, Object> retMap =new HashMap<>();
        Map<String,String> messMap =objectMapper.readValue(data, Map.class);
        MH5Message mH5Message = new MH5Message();
        mH5Message.setAgencyAbb(agencyAbb);
        mH5Message.setAppId(appId);
        mH5Message.setData(messMap);
        if(null != isSuccess){
            mH5Message.setIsSuccess(isSuccess);
        }else{
            //失败
            retMap.put("status","1");
            retMap.put("statusInfo","isSuccess 不能为空!");
            retMap.put("t", System.currentTimeMillis());
            LOG.error("isSuccess not null");
        }
        mH5Message.setOrderId(orderId);
        mH5Message.setSign(sign);
        mH5Message.setThirdPartyOrderId(thirdPartyOrderId);
        mH5Message.setThirdPartyUserId(thirdPartyUserId);
        mH5Message.setType(type);
        mH5Message.setTimestamp(timestamp);
        LOG.info(String.format("收到H5挂号订单消息推送参数内容-%s", toJson(mH5Message)));
        //根据用户id，到总部获取订单列表
        MProtalOrderMessage mProtalOrderMessage = openServiceGetOrderInfo(thirdPartyUserId,timestamp);
        Registration newEntity = null;
        if(null != mProtalOrderMessage && mProtalOrderMessage.getTotal()>0){
            String str = toJson(mProtalOrderMessage.getResult().get(0));
            newEntity = new Registration();
            newEntity = objectMapper.readValue(str, Registration.class);
            newEntity.setId(UuidUtil.randomUUID());
            newEntity.setOriginType(2);//app端订单
            newEntity.setRegisterType(1);//预约挂号
            registrationService.save(newEntity);
        }
        ProtalMessageRemind protalMessageRemind = null;
        if(StringUtils.isNotEmpty(data)){
            List<PortalMessageTemplate> messageTemplateList = messageTemplateService.getMessageTemplate(String.valueOf(isSuccess),String.valueOf(type),"0");
            long messageTemplateId = 1;
            if(null != messageTemplateList && messageTemplateList.size()>0){
                messageTemplateId = messageTemplateList.get(0).getId();
                mH5Message.setPortalMessagerTemplateType(messageTemplateList.get(0).getType());
            }else{
                retMap.put("status","1");
                retMap.put("statusInfo","消息模板不存在！");
                retMap.put("t", System.currentTimeMillis());
                LOG.info("消息模板不存在！");
            }
            protalMessageRemind = messageTemplateService.saveH5MessagePush(newEntity,mH5Message, messageTemplateId);
            if (protalMessageRemind != null){
                //成功
                retMap.put("status","0");
                retMap.put("statusInfo",null);
                retMap.put("t", System.currentTimeMillis());
            } else{
                //失败
                retMap.put("status","1");
                retMap.put("statusInfo","消息解析失败");
                retMap.put("t", System.currentTimeMillis());
                LOG.error("消息解析失败");
            }
        } else{
            //失败
            retMap.put("status","1");
            retMap.put("statusInfo","data消息缺失！");
            retMap.put("t", System.currentTimeMillis());
            LOG.error("data消息缺失！");
        }
        return toJson(retMap);
    }

    /**
     * 调总部接口，询挂号单列表
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "MessageOrderPush", method = RequestMethod.POST)
    @ApiOperation(value = "根据用户id和时间戳获取挂号订单", notes = "根据用户id和时间戳获取挂号订单")
    private MProtalOrderMessage openServiceGetOrderInfo(String thirdPartyUserId, Long begIntime) throws  Exception{
        SimpleDateFormat format =  new SimpleDateFormat(DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
        long time = 60*1000;//
        String afterDate = format.format(new Date(new Date(begIntime).getTime() + time));//1分钟后的时间
        String beforeDate = format.format(new Date(new Date(begIntime) .getTime() - time));//1分钟前的时间
        String api="TradeMgmt/Open/queryRegOrderInfos";
        Map<String, Object> params=new HashMap<>();
        params.put("thirdPartyUserId",thirdPartyUserId);
        params.put("begIntime", beforeDate);
        params.put("endTime",afterDate);
        params.put("pageIndex",1);
        params.put("pageSize",5);
       String res = openService.callFzOpenApi(api,params);
        MProtalOrderMessage mProtalOrderMessage =  objectMapper.readValue(res,MProtalOrderMessage.class);
        LOG.info(String.format("-----MProtalOrderMessage-----%s", toJson(mProtalOrderMessage)));
        return mProtalOrderMessage;
    }


}
