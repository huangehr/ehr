package com.yihu.ehr.basic.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.basic.appointment.service.RegistrationService;
import com.yihu.ehr.basic.org.model.OrgDeptDetail;
import com.yihu.ehr.basic.org.service.OrgDeptDetailService;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.basic.portal.service.PortalMessageTemplateService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.model.portal.MProtalOrderMessage;
import com.yihu.ehr.model.portal.MRegistration;
import com.yihu.ehr.model.redis.MRedisMqChannel;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "messageRemind", description = "消息提醒管理服务", tags = {"云门户-待办事项"})
public class PortalMessageRemindEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PortalMessageRemindService messageRemindService;
    @Autowired
    private OrgDeptDetailService deptDetailService;
    @Autowired
    private PortalMessageTemplateService portalMessageTemplateService;
    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public List<MMessageRemind> getMessageRemindTop10(){
        List<ProtalMessageRemind> list = messageRemindService.getMessageRemindTop10();
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
        List<ProtalMessageRemind> messageRemindList = messageRemindService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, messageRemindService.getCount(filters), page, size);
        return (List<MMessageRemind>) convertToModels(messageRemindList, new ArrayList<MMessageRemind>(messageRemindList.size()), MMessageRemind.class, fields);
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建提醒消息", notes = "创建提醒消息信息")
    public MMessageRemind createMessageRemind(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestBody String messageRemindJsonData) throws Exception {
        ProtalMessageRemind messageRemind = toEntity(messageRemindJsonData, ProtalMessageRemind.class);
        messageRemind.setCreateDate(new Date());
        messageRemindService.save(messageRemind);
        return convertToModel(messageRemind, MMessageRemind.class);
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改提醒消息", notes = "重新绑定提醒消息")
    public MMessageRemind updateMessageRemind(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestBody String messageRemindJsonData) throws Exception {
        ProtalMessageRemind messageRemind = toEntity(messageRemindJsonData, ProtalMessageRemind.class);
        messageRemindService.save(messageRemind);
        return convertToModel(messageRemind, MMessageRemind.class);
    }



    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除提醒消息", notes = "根据id删除提醒消息")
    public boolean deleteMessageRemind(
            @ApiParam(name = "messageRemind_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "messageRemind_id") Long messageRemindId) throws Exception {
        messageRemindService.deleteMessageRemind(messageRemindId);
        return true;
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindReaded, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "阅读-消息信息")
    public MMessageRemind readMessage(
            @ApiParam(name = "remindId", value = "消息ID")
            @PathVariable(value = "remindId") Long remindId) throws Exception {
        ProtalMessageRemind remind = messageRemindService.updateRemindReaded(remindId);
        if(remind == null){
            return null;
        }
        return convertToModel(remind, MMessageRemind.class);
    }


    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息")
    public MMessageRemind getMessageRemindInfo(
            @ApiParam(name = "messageRemind_id", value = "", defaultValue = "")
            @PathVariable(value = "messageRemind_id") Long messageRemindId) {
        ProtalMessageRemind messageRemind = messageRemindService.getMessageRemind(messageRemindId);
        MMessageRemind messageRemindModel = convertToModel(messageRemind, MMessageRemind.class);
        return messageRemindModel;
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindCount, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取提醒消息信息")
    public int getMessageRemindCount(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters )throws ParseException {
        long num  = messageRemindService.getCount(filters);
        return Integer.parseInt(String.valueOf(num));
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageReminInfodByType, method = RequestMethod.GET)
    @ApiOperation(value = "按分类获取提醒消息-我的就诊详情", notes = "根据按分类获取提醒消息-我的就诊详情")
    public Envelop searchMessageRemindInfoByType(
            @ApiParam(name = "protalMessageRemindId", value = "消息id")
            @RequestParam(value = "protalMessageRemindId", required = false) Long protalMessageRemindId,
            @ApiParam(name = "orderId", value = "健康之路-订单id", defaultValue = "18551095183243")
            @RequestParam(value = "orderId", required = false) String orderId) throws Exception {
        Envelop envelop = new Envelop();
        //点开详情之后，更新消息已读、不再通知状态
        messageRemindService.updateMessageRemind("readed","1",protalMessageRemindId);
        messageRemindService.updateMessageRemind("notifie_flag","1",protalMessageRemindId);
        //如果type为空的话，默认获取当前用户的所有消息。否则获取指定消息模板的消息。
        ProtalMessageRemind  protalMessageRemind = messageRemindService.getMessageRemind(protalMessageRemindId);
        PortalMessageTemplate template = portalMessageTemplateService.getMessageTemplate(protalMessageRemind.getMessageTemplateId());
        //根据订单号获取 订单详情
        List<Registration> registrationList=  registrationService.findByField("orderId",orderId);
        List<MRegistration> mRegistrationlList = (List<MRegistration>) convertToModels(registrationList, new ArrayList<MRegistration>(), MRegistration.class, "");
        MMessageRemind mMessageRemind = new MMessageRemind();
        //提供将挂号单详细内容
       if(null != mRegistrationlList && mRegistrationlList.size()>0){
            MRegistration mregistration =mRegistrationlList.get(0);
           //根据医院名称、科室名称查找科室位置
           String orgName = mregistration.getHospitalName() == null ? "" : mregistration.getHospitalName();
           String deptName = mregistration.getDeptName() == null ? "" : mregistration.getDeptName();
           OrgDeptDetail orgDeptDetail = null;
           if(StringUtils.isNotEmpty(orgName)&&StringUtils.isNotEmpty(deptName)){
               orgDeptDetail = deptDetailService.searchByOrgNameAndDeptName(orgName,deptName);
               //科室位置
               mMessageRemind.setDeptAdress(orgDeptDetail == null?"":orgDeptDetail.getPlace());
           }
           //温馨提示
           mMessageRemind.setNotice(template == null?"":template.getAfterContent());
           mMessageRemind.setmRegistration(mregistration);
           envelop.setObj(mMessageRemind);
       }
        envelop.setSuccessFlg(true);
        return envelop;
    }


    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindByNotifie, method = RequestMethod.GET)
    @ApiOperation(value = "公众健康服务-列表-我的就诊", notes = "公众健康服务-消息列表-我的就诊")
    public Envelop searchMessageRemindByNotifie(
            @ApiParam(name = "type", value = "模板消息类型：101：挂号结果推送，102：退号结果推送，-101：订单操作推送，100满意度调查", defaultValue = "101")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "appId", value = "应用id", defaultValue = "WYo0l73F8e")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "toUserId", value = "当前用户id", defaultValue = "0dae00035ab8be56319e6d2e0f183443")
            @RequestParam(value = "toUserId", required = false) String toUserId,
            @ApiParam(name = "typeId", value = "消息类型，默认7为健康上饶app的消息", defaultValue = "7")
            @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "3")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "notifie", value = "是否通知：0为通知，1为不再通知", defaultValue = "0")
            @RequestParam(value = "notifie", required = false) String notifie) throws Exception {
        Envelop envelop = new Envelop();
        List<MMessageRemind> messageRemindList = new ArrayList<>();
        DataList list = messageRemindService.listMessageRemindValue(appId,toUserId,typeId,type,page,size,notifie);
        //模板消息类型：101：挂号结果推送，获取订单信息
        if("101".equals(type)){
            MMessageRemind  mMessageRemind = null;
             new Registration();
            if(null != list && list.getSize()>0){
                for(int i=0;i<list.getList().size();i++){
                    Map<String,Object> dataMap = (Map<String, Object>) list.getList().get(i);
                    MRegistration newEntity = objectMapper.readValue(toJson(dataMap), MRegistration.class);
                    //根据订单号获取 消息
                    List<ProtalMessageRemind> protalMessageR=  messageRemindService.findByField("orderId",newEntity.getOrderId());
                    mMessageRemind =new MMessageRemind();
                    mMessageRemind.setmRegistration(convertToModel(newEntity, MRegistration.class));
                    if(null != protalMessageR && protalMessageR.size()>0){
                    mMessageRemind.setReaded(Integer.valueOf(protalMessageR.get(0).getReaded()));
                    mMessageRemind.setId(Long.parseLong(protalMessageR.get(0).getId().toString()));
                    mMessageRemind.setOrderId(newEntity.getOrderId());
                    mMessageRemind.setNotifieFlag(protalMessageR.get(0).getNotifieFlag());
                    }
                    messageRemindList.add(mMessageRemind);
                }
            }
        }else{
            for(int i=0;i<list.getList().size();i++) {
                Map<String, Object> dataMap = (Map<String, Object>) list.getList().get(i);
                MMessageRemind mMessageRemind = objectMapper.readValue(toJson(dataMap), MMessageRemind.class);
                messageRemindList.add(mMessageRemind);
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(messageRemindList);
        envelop.setPageSize(size);
        envelop.setCurrPage(page);
        envelop.setTotalPage((int)list.getPage());
        envelop.setTotalCount((int)list.getCount());
        return envelop;
    }

    @RequestMapping(value = ServiceApi.MessageRemind.UpdateMessageRemindByNotifie, method = RequestMethod.GET)
    @ApiOperation(value = "更新-我的就诊通知状态", notes = "更新-我的就诊通知状态")
    public Envelop UpdateMessageRemindByNotifie(
            @ApiParam(name = "appId", value = "应用id", defaultValue = "WYo0l73F8e")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "toUserId", value = "当前用户id", defaultValue = "0dae00035ab8be56319e6d2e0f183443")
            @RequestParam(value = "toUserId", required = false) String toUserId,
            @ApiParam(name = "typeId", value = "消息类型，默认7为健康上饶app的消息", defaultValue = "7")
            @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(name = "protalMessageRemindId", value = "消息id")
            @RequestParam(value = "protalMessageRemindId", required = false) Long protalMessageRemindId,
            @ApiParam(name = "notifie", value = "是否通知：0为通知，1为不再通知", defaultValue = "0")
            @RequestParam(value = "notifie", required = false) String notifie) throws Exception {
        Envelop envelop = new Envelop();
        DataList list = messageRemindService.listMessageRemindValue(appId,toUserId,typeId,"",1,10,"0");
        if(null != list && null !=list.getList() && list.getList().size()<5){
            messageRemindService.updateMessageRemind("notifie_flag",notifie,protalMessageRemindId);
            envelop.setSuccessFlg(true);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，首页消息只能提醒5条！");
        }
        return envelop;
    }

    @Override
    protected String toJson(Object obj) throws JsonProcessingException {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper.writeValueAsString(obj);
    }
}
