package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.MessageRemindModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.portal.service.MessageRemindClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "itResource", description = "消息管理接口", tags = {"消息管理接口"})
public class MessageRemindController extends BaseController {

    @Autowired
    private MessageRemindClient remindClient;

    @RequestMapping(value = "/messageRemind/list", method = RequestMethod.POST)
    @ApiOperation(value = "获取消息列表", notes = "根据查询条件获取消息列表在前端表格展示")
    public Envelop searchMessages(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,appId,content,createDate")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {
            List<MessageRemindModel> messageModels = new ArrayList<>();
            ResponseEntity<List<MMessageRemind>> responseEntity = remindClient.searchMessages(fields, filters, sorts, size, page);
            List<MMessageRemind> messages = responseEntity.getBody();
            for (MMessageRemind message : messages) {
                MessageRemindModel messageModel = convertToModel(message, MessageRemindModel.class);
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
            @ApiParam(name = "messageJsonData", value = " 消息信息Json", defaultValue = "")
            @RequestParam(value = "messageJsonData", required = false) String messageJsonData) {
        try {
            String errorMsg = "";
            MessageRemindModel messageModel = objectMapper.readValue(messageJsonData, MessageRemindModel.class);
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
            MMessageRemind newMessage = remindClient.saveMessage(messageJsonStr);
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
    @ApiOperation(value = "阅读消息详情")
    public Envelop readMessage(
            @ApiParam(name = "messageId", value = " 消息ID")
            @RequestParam(value = "messageId", required = true) Integer messageId) {
        try {
            if (messageId == null) {
                return failed("应用ID不能为空！");
            }

            MMessageRemind message = remindClient.readMessage(messageId);
            if (message == null) {
                return failed("阅读消息失败!");
            }
            return success(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


}
