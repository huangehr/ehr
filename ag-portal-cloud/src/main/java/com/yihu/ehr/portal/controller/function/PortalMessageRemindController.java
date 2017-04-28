package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.agModel.portal.MessageRemindModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MMessageRemind;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.portal.service.function.PortalMessageRemindClient;
import com.yihu.ehr.portal.service.function.UserClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ysj on 2017年2月20日
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "PortalMessageRemind", description = "PortalMessageRemind", tags = {"云门户-待办事项"})
public class PortalMessageRemindController extends BaseController {

    @Autowired
    PortalMessageRemindClient portalMessageRemindClient;
    @Autowired
    private UserClient userClient;

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public Envelop getPortalMessageRemindTop10(){
        ResponseEntity<List<MMessageRemind>> responseEntity = portalMessageRemindClient.getMessageRemindTop10();
        List<MMessageRemind> mPortalMessageRemindList = responseEntity.getBody();
        List<MessageRemindModel> portalMessageRemindModels = new ArrayList<>();
        for (MMessageRemind mPortalMessageRemind : mPortalMessageRemindList) {
            MessageRemindModel portalMessageRemindModel = convertToModel(mPortalMessageRemind, MessageRemindModel.class);
            portalMessageRemindModel.setCreateDate(mPortalMessageRemind.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mPortalMessageRemind.getCreateDate()));
            if (StringUtils.isNotEmpty(portalMessageRemindModel.getFromUserId()) ){
                MUser mUser = userClient.getUser(portalMessageRemindModel.getFromUserId());
                mPortalMessageRemind.setFromUserName(mUser == null ? "" : mUser.getRealName());
            }
            portalMessageRemindModels.add(portalMessageRemindModel);
        }

        Envelop envelop = getResult(portalMessageRemindModels, mPortalMessageRemindList.size(), 1, 10);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒列表", notes = "根据查询条件获取消息提醒列表在前端表格展示")
    public Envelop searchPortalMessageRemind(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "readed", required = false) String readed,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    ) throws Exception{
        String filters = "";
        if(!StringUtils.isEmpty(userId)){
            filters += "toUserId="+userId+";";
        }
        if(!StringUtils.isEmpty(readed)){
            filters += "readed="+readed+";";
        }
        String sorts = "+createDate";
        ResponseEntity<List<MMessageRemind>> responseEntity = portalMessageRemindClient.searchMessageRemind(null, filters, sorts, size, page);
        List<MMessageRemind> mPortalMessageRemindList = responseEntity.getBody();
        List<MessageRemindModel> portalMessageRemindModels = new ArrayList<>();
        for (MMessageRemind mPortalMessageRemind : mPortalMessageRemindList) {
            MessageRemindModel portalMessageRemindModel = convertToModel(mPortalMessageRemind, MessageRemindModel.class);
            portalMessageRemindModel.setCreateDate(mPortalMessageRemind.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mPortalMessageRemind.getCreateDate()));
            if (StringUtils.isNotEmpty(portalMessageRemindModel.getFromUserId()) ){
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


    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息数量", notes = "获取消息数量")
    public Envelop getPortalMessageRemindCount(
            @ApiParam(name = "userId", value = "用戶ID", defaultValue = "")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "readed", value = "阅读标识", defaultValue = "0")
            @RequestParam(value = "readed") int readed) {
        String filters = "";
        if(!StringUtils.isEmpty(userId)){
            filters += "toUserId="+userId+";";
        }
        filters += "readed="+readed+";";
        int num = portalMessageRemindClient.getMessageRemindCount(filters);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(num);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息", notes = "消息提醒信息")
    public Envelop getPortalMessageRemind(@PathVariable(value = "portalMessageRemind_id") Long portalMessageRemindId){
        try {
            MMessageRemind mPortalMessageRemind = portalMessageRemindClient.getMessageRemind(portalMessageRemindId);
            if (mPortalMessageRemind == null) {
                return failed("消息提醒信息获取失败!");
            }

            MessageRemindModel detailModel = convertToModel(mPortalMessageRemind, MessageRemindModel.class);
            detailModel.setCreateDate(mPortalMessageRemind.getCreateDate() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mPortalMessageRemind.getCreateDate()));

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindReaded, method = RequestMethod.GET)
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

    //yyyy-MM-dd HH:mm:ss 转换为yyyy-MM-dd'T'HH:mm:ss'Z 格式
    public String changeToUtc(String datetime) throws Exception{
        Date date = DateTimeUtil.simpleDateTimeParse(datetime);
        return DateTimeUtil.utcDateTimeFormat(date);
    }

}
