package com.yihu.ehr.basic.portal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.model.redis.MRedisMqChannel;
import com.yihu.ehr.query.common.model.DataList;
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
        messageRemind.setCreate_date(new Date());
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

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindByType, method = RequestMethod.GET)
    @ApiOperation(value = "按分类获取提醒消息列表", notes = "根据按分类获取提醒消息列表")
    public Envelop searchMessageRemindByType(
            @ApiParam(name = "type", value = "模板消息类型：101：挂号结果推送，102：退号结果推送，-101：订单操作推送，100满意度调查", defaultValue = "101")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "appId", value = "应用id", defaultValue = "WYo0l73F8e")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "toUserId", value = "当前用户id", defaultValue = "")
            @RequestParam(value = "toUserId", required = false) String toUserId,
            @ApiParam(name = "typeId", value = "消息类型，默认7为健康上饶app的消息", defaultValue = "7")
            @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "3")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        Envelop envelop = new Envelop();
        List<ProtalMessageRemind> messageRemindList = new ArrayList<>();
        //如果type为空的话，默认获取当前用户的所有消息。否则获取指定消息模板的消息。
        DataList list = messageRemindService.listMessageRemindValue(appId,toUserId,typeId,type,page,size);
        ProtalMessageRemind  protalMessageRemind = null;
        if(null != list && list.getSize()>0){
                for(int i=0;i<list.getList().size();i++){
                    Map<String,Object> dataMap = (Map<String, Object>) list.getList().get(i);
                    protalMessageRemind = new ProtalMessageRemind();
                    if(null !=dataMap.get("content")){
                        List<Map<String,String>> mapList = JSONArray.parseObject(dataMap.get("content").toString(),List.class);
                        if(null != mapList && mapList.size()>0){
                            for(Map<String,String> mapL : mapList){
                                for (Map.Entry entry : mapL.entrySet()){
                                    protalMessageRemind.setContent(entry.getValue().toString());
                                }
                            }
                        }
                    }else{
                        protalMessageRemind.setContent("我的就诊信息");
                    }
                    protalMessageRemind.setReaded(Integer.valueOf(dataMap.get("readed").toString()));
                    messageRemindList.add(protalMessageRemind);
                }
            }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(messageRemindList);
        envelop.setPageSize(size);
        envelop.setCurrPage(page);
        return envelop;
    }
}
