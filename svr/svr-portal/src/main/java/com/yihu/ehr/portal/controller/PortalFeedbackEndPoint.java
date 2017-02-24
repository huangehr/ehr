package com.yihu.ehr.portal.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MPortalFeedback;
import com.yihu.ehr.portal.model.PortalFeedback;
import com.yihu.ehr.portal.service.PortalFeedbackService;
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
import java.util.List;

/**
 * 2017-02-21 add  by ysj
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "portalFeedback", description = "意见反馈接口")
public class PortalFeedbackEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PortalFeedbackService portalFeedbackService;


    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈列表", notes = "根据查询条件获取意见反馈列表在前端表格展示")
    public List<MPortalFeedback> searchPortalFeedback(
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
        List<PortalFeedback> portalFeedbackList = portalFeedbackService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalFeedbackService.getCount(filters), page, size);

        return (List<MPortalFeedback>) convertToModels(portalFeedbackList, new ArrayList<MPortalFeedback>(portalFeedbackList.size()), MPortalFeedback.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建意见反馈", notes = "创建意见反馈信息")
    public MPortalFeedback createPortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestBody String portalFeedbackJsonData) throws Exception {
        PortalFeedback portalFeedback = toEntity(portalFeedbackJsonData, PortalFeedback.class);

        portalFeedbackService.save(portalFeedback);
        return convertToModel(portalFeedback, MPortalFeedback.class);
    }

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改意见反馈", notes = "重新绑定意见反馈信息")
    public MPortalFeedback updatePortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestBody String portalFeedbackJsonData) throws Exception {
        PortalFeedback portalFeedback = toEntity(portalFeedbackJsonData, PortalFeedback.class);
        portalFeedbackService.save(portalFeedback);
        return convertToModel(portalFeedback, MPortalFeedback.class);
    }


    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedbackAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取意见反馈信息")
    public MPortalFeedback getPortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") Long portalFeedbackId) {
        PortalFeedback portalFeedback = portalFeedbackService.getPortalFeedback(portalFeedbackId);
        MPortalFeedback portalFeedbackModel = convertToModel(portalFeedback, MPortalFeedback.class);
        return portalFeedbackModel;
    }


    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedbackAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除意见反馈", notes = "根据id删除意见反馈")
    public boolean deletePortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "意见反馈编号", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") Long portalFeedbackId) throws Exception {
        portalFeedbackService.deletePortalFeedback(portalFeedbackId);
        return true;
    }

}