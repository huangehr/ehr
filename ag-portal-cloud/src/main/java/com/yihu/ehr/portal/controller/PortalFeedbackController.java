package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalFeedbackModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MPortalFeedback;
import com.yihu.ehr.portal.service.PortalFeedbackClient;
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
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/doctor")
@RestController
@Api(value = "portalFeedback", description = "PortalFeedback", tags = {"意见反馈接口"})
public class PortalFeedbackController extends BaseController{

    @Autowired
    private PortalFeedbackClient portalFeedbackClient;


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈列表", notes = "根据查询条件获取意见反馈列表在前端表格展示")
    public Envelop searchPortalFeedback(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MPortalFeedback>> responseEntity = portalFeedbackClient.searchPortalFeedback(fields, filters, sorts, size, page);
        List<MPortalFeedback> mPortalFeedbackList = responseEntity.getBody();
        List<PortalFeedbackModel> portalFeedbackModels = new ArrayList<>();
        for (MPortalFeedback mPortalFeedback : mPortalFeedbackList) {
            PortalFeedbackModel portalFeedbackModel = convertToModel(mPortalFeedback, PortalFeedbackModel.class);
            portalFeedbackModel.setReplyDate(mPortalFeedback.getReplyDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalFeedback.getReplyDate()));
            portalFeedbackModel.setSubmitDate(mPortalFeedback.getSubmitDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalFeedback.getSubmitDate()));

            portalFeedbackModels.add(portalFeedbackModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalFeedbackModels, totalCount, page, size);
        return envelop;
    }


    @RequestMapping(value = "portalFeedback/admin/{portalFeedback_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈信息", notes = "意见反馈信息")
    public Envelop getPortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") Long portalFeedbackId) {
        try {
            MPortalFeedback mPortalFeedback = portalFeedbackClient.getPortalFeedback(portalFeedbackId);
            if (mPortalFeedback == null) {
                return failed("意见反馈信息获取失败!");
            }

            PortalFeedbackModel detailModel = convertToModel(mPortalFeedback, PortalFeedbackModel.class);
            detailModel.setReplyDate(mPortalFeedback.getReplyDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalFeedback.getReplyDate()));
            detailModel.setSubmitDate(mPortalFeedback.getSubmitDate()==null?"":DateTimeUtil.simpleDateTimeFormat(mPortalFeedback.getSubmitDate()));

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/portalFeedback/admin/{portalFeedback_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除意见反馈", notes = "根据意见反馈id删除")
    public Envelop deletePortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "意见反馈编号", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") String portalFeedbackId) {
        try {
            boolean result = portalFeedbackClient.deletePortalFeedback(portalFeedbackId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.POST)
    @ApiOperation(value = "创建意见反馈", notes = "重新绑定意见反馈信息")
    public Envelop createPortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalFeedback_json_data") String portalFeedbackJsonData) {
        try {
            PortalFeedbackModel detailModel = objectMapper.readValue(portalFeedbackJsonData, PortalFeedbackModel.class);

            String errorMsg = null;


            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalFeedback mPortalFeedback = convertToMPortalFeedback(detailModel);
            mPortalFeedback = portalFeedbackClient.createPortalFeedback(objectMapper.writeValueAsString(mPortalFeedback));
            if (mPortalFeedback == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalFeedback, PortalFeedbackModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.PUT)
    @ApiOperation(value = "修改意见反馈", notes = "重新绑定意见反馈信息")
    public Envelop updatePortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalFeedback_json_data") String portalFeedbackJsonData) {
        try {
            PortalFeedbackModel detailModel = objectMapper.readValue(portalFeedbackJsonData, PortalFeedbackModel.class);
            String errorMsg = null;

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalFeedback mPortalFeedback = convertToMPortalFeedback(detailModel);
            mPortalFeedback = portalFeedbackClient.updatePortalFeedback(objectMapper.writeValueAsString(mPortalFeedback));
            if(mPortalFeedback==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalFeedback, PortalFeedbackModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public MPortalFeedback convertToMPortalFeedback(PortalFeedbackModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MPortalFeedback mPortalFeedback = convertToModel(detailModel,MPortalFeedback.class);
        mPortalFeedback.setReplyDate(DateTimeUtil.simpleDateTimeParse(detailModel.getReplyDate()));
        mPortalFeedback.setSubmitDate(DateTimeUtil.simpleDateTimeParse(detailModel.getSubmitDate()));

        return mPortalFeedback;
    }
}
