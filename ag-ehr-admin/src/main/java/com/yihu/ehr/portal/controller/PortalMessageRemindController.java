package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.MessageRemindModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.portal.client.PortalMessageRemindClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@RestController
@Api(value = "PortalMessageRemindController", description = "待办事项管理", tags = {"云门户-待办事项管理"})
public class PortalMessageRemindController extends BaseController {

    @Autowired
    private PortalMessageRemindClient portalMessageRemindClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    private UserClient userClient;

    @RequestMapping(value = ServiceApi.Portal.MessageRemind, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒列表", notes = "根据查询条件获取消息提醒列表在前端表格展示")
    public Envelop searchPortalMessageRemind(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "readed", required = false) String readed,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    ) throws Exception{
        String filters = "";
        if(!org.apache.commons.lang.StringUtils.isEmpty(userId)){
            filters += "toUserId="+userId+";";
        }
        if(!org.apache.commons.lang.StringUtils.isEmpty(readed)){
            filters += "readed="+readed+";";
        }
        String sorts = "+createDate";
        ResponseEntity<List<MMessageRemind>> responseEntity = portalMessageRemindClient.searchMessageRemind(null, filters, sorts, size, page);
        List<MMessageRemind> mPortalMessageRemindList = responseEntity.getBody();
        List<MessageRemindModel> portalMessageRemindModels = new ArrayList<>();
        for (MMessageRemind mPortalMessageRemind : mPortalMessageRemindList) {
            MessageRemindModel portalMessageRemindModel = convertToModel(mPortalMessageRemind, MessageRemindModel.class);
            portalMessageRemindModel.setCreateDate(mPortalMessageRemind.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mPortalMessageRemind.getCreateDate()));
            if (org.apache.commons.lang.StringUtils.isNotEmpty(portalMessageRemindModel.getFromUserId()) ){
                MUser mUser = userClient.getUser(portalMessageRemindModel.getFromUserId());
                portalMessageRemindModel.setFromUserName(mUser == null ? "" : mUser.getRealName());
            }
            portalMessageRemindModels.add(portalMessageRemindModel);
        }
        //获取总条数
        int totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(portalMessageRemindModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Portal.MessageRemindCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息数量", notes = "获取消息数量")
    public Envelop getPortalMessageRemindCount(
            @ApiParam(name = "userId", value = "用戶ID", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "readed", value = "阅读标识", defaultValue = "0")
            @RequestParam(value = "readed") int readed) {
        String filters = "";
        if(!org.apache.commons.lang.StringUtils.isEmpty(userId)){
            filters += "toUserId="+userId+";";
        }
        filters += "readed="+readed+";";
        int num = portalMessageRemindClient.getMessageRemindCount(filters);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(num);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Portal.MessageRemindRead, method = RequestMethod.GET)
    @ApiOperation(value = "更新待办事项的阅读状态", notes = "更新待办事项的阅读状态")
    public Envelop updateMsgRemindReaded(@PathVariable(value = "remindId") Long remindId){
        try {
            MMessageRemind mPortalMessageRemind = portalMessageRemindClient.updateMsgRemindReaded(remindId);
            if (mPortalMessageRemind == null) {
                return failed("消息状态更新失败!");
            }
            return success("消息状态更新成功");
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    //-------------------------------------  以上为云平台网关移动过来的接口 ----------------------------------

    @RequestMapping(value = "/messageRemindList", method = RequestMethod.GET)
    @ApiOperation(value = "获取消息列表", notes = "根据查询条件获取消息列表在前端表格展示")
    public Envelop searchMessages(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,appId,content,createDate")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+createDate,+readed")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {
            List<MessageRemindModel> messageModels = new ArrayList<>();
            ResponseEntity<List<MMessageRemind>> responseEntity = portalMessageRemindClient.searchMessageRemind(fields, filters, sorts, size, page);
            List<MMessageRemind> messages = responseEntity.getBody();
            for (MMessageRemind message : messages) {
                MessageRemindModel messageModel = convertToModel(message, MessageRemindModel.class);
                if (StringUtils.isNotEmpty(message.getFromUserId()) ){
                    MUser mUser = userClient.getUser(message.getFromUserId());
                    messageModel.setFromUserName(mUser == null ? "" : mUser.getRealName());
                }
                if (StringUtils.isNotEmpty(message.getToUserId()) ){
                    MUser mUser = userClient.getUser(message.getToUserId());
                    messageModel.setToUserName(mUser == null ? "" : mUser.getRealName());
                }
                messageModel.setCreateDate(message.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(message.getCreateDate()));
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getMessageRemindTypeList(String.valueOf(messageModel.getTypeId()));
                messageModel.setTypeName(dict == null ? "" : dict.getValue());
                messageModels.add(messageModel);
            }
            int totalCount = getTotalCount(responseEntity);
            return getResult(messageModels, totalCount, page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/messageRemind", method = RequestMethod.POST)
    @ApiOperation(value = "新增消息信息")
    public Envelop createMessage(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestParam(value = "messageRemind_json_data") String messageRemindJsonData) {
        try {
            String errorMsg = "";
            MessageRemindModel messageModel = objectMapper.readValue(messageRemindJsonData, MessageRemindModel.class);
            MMessageRemind mMessage = convertToModel(messageModel, MMessageRemind.class);
            if (mMessage.getAppId() == null) {
                errorMsg += "应用ID不能为空！";
            }
            if (StringUtils.isEmpty(mMessage.getContent())) {
                errorMsg += "消息内容不能为空！";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            String messageJsonStr = objectMapper.writeValueAsString(mMessage);
            MMessageRemind newMessage = portalMessageRemindClient.createMessageRemind(messageJsonStr);
            if (newMessage == null) {
                return failed("保存失败!");
            }
            return success(newMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/messageRemind", method = RequestMethod.PUT)
    @ApiOperation(value = "修改提醒消息", notes = "重新绑定提醒消息信息")
    public Envelop updateMessageRemind(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestParam(value = "messageRemind_json_data") String messageRemindJsonData) {
        try {
            MessageRemindModel detailModel = toEntity(messageRemindJsonData, MessageRemindModel.class);
            String errorMsg = "";
            if (detailModel.getAppId() == null) {
                errorMsg += "应用ID不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getContent())) {
                errorMsg += "消息内容不能为空！";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MMessageRemind mMessageRemind = convertToMMessageRemind(detailModel);
            mMessageRemind = portalMessageRemindClient.updateMessageRemind(objectMapper.writeValueAsString(mMessageRemind));
            if(mMessageRemind==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mMessageRemind, MessageRemindModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public MMessageRemind convertToMMessageRemind(MessageRemindModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MMessageRemind mMessageRemind = convertToModel(detailModel,MMessageRemind.class);
        mMessageRemind.setCreateDate(DateTimeUtil.simpleDateTimeParse(detailModel.getCreateDate()));
        return mMessageRemind;
    }

    @RequestMapping(value = "messageRemind/admin/{messageRemind_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息信息", notes = "提醒消息信息")
    public Envelop getMessageRemind(
            @ApiParam(name = "messageRemind_id", value = "", defaultValue = "")
            @PathVariable(value = "messageRemind_id") Long messageRemindId) {
        try {
            MMessageRemind mMessageRemind = portalMessageRemindClient.getMessageRemind(messageRemindId);
            if (mMessageRemind == null) {
                return failed("提醒消息信息获取失败!");
            }else{
                if (StringUtils.isNotEmpty(mMessageRemind.getToUserId()) ){
                    MUser mUser = userClient.getUser(mMessageRemind.getToUserId());
                    mMessageRemind.setToUserName(mUser == null ? "" : mUser.getRealName());
                }
            }
            MessageRemindModel detailModel = convertToModel(mMessageRemind, MessageRemindModel.class);
            detailModel.setCreateDate(mMessageRemind.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mMessageRemind.getCreateDate()));
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/messageRemind/admin/{messageRemind_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除提醒消息", notes = "根据提醒消息id")
    public Envelop deleteMessageRemind(
            @ApiParam(name = "messageRemind_id", value = "提醒消息编号", defaultValue = "")
            @PathVariable(value = "messageRemind_id") String messageRemindId) {
        try {
            boolean result = portalMessageRemindClient.deleteMessageRemind(messageRemindId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


}
