package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MMessageRemind;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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
@Api(value = "messageRemind", description = "消息提醒管理服务", tags = {"云门户-待办事项"})
public class PortalMessageRemindEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PortalMessageRemindService messageRemindService;

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
}
