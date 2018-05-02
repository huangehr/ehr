package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.model.PortalFeedback;
import com.yihu.ehr.basic.portal.service.PortalFeedbackService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.portal.MPortalFeedback;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2017-02-21 add  by ysj
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "portalFeedback", description = "意见反馈接口", tags = {"云门户-意见反馈接口"})
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
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "-submitDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        if(StringUtils.isEmpty(sorts)){
            sorts = "-submitDate";
        }
        List<PortalFeedback> portalFeedbackList = portalFeedbackService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalFeedbackService.getCount(filters), page, size);

        return (List<MPortalFeedback>) convertToModels(portalFeedbackList, new ArrayList<MPortalFeedback>(portalFeedbackList.size()), MPortalFeedback.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建意见反馈", notes = "创建意见反馈信息")
    public MPortalFeedback createPortalFeedback(
            @ApiParam(name = "portalFeedbackJsonData", value = "", defaultValue = "")
            @RequestBody String portalFeedbackJsonData) throws Exception {
        PortalFeedback portalFeedback = toEntity(portalFeedbackJsonData, PortalFeedback.class);
        portalFeedback.setSubmitDate(DateUtil.getSysDateTime());
        portalFeedbackService.save(portalFeedback);
        return convertToModel(portalFeedback, MPortalFeedback.class);
    }

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改意见反馈", notes = "重新绑定意见反馈信息")
    public MPortalFeedback updatePortalFeedback(
            @ApiParam(name = "portalFeedbackJsonData", value = "", defaultValue = "")
            @RequestBody String portalFeedbackJsonData) throws Exception {
        PortalFeedback portalFeedback = toEntity(portalFeedbackJsonData, PortalFeedback.class);
        portalFeedback.setSubmitDate(DateUtil.getSysDateTime());
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

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedBackByUserId, method = RequestMethod.GET)
    @ApiOperation(value = "获取个人意见反馈列表", notes = "根据userId查询意见反馈列表")
    public ListResult findFeedbackByUserId(
            @ApiParam(name = "userId", value = "用户id") @RequestParam(value = "userId") String userId,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ListResult listResult = new ListResult();
        Map<String,Object> map = portalFeedbackService.findByUserId(userId,page,size);
        if(map != null){
            listResult.setDetailModelList((List<PortalFeedback>)map.get("data"));
            listResult.setTotalCount((int)map.get("count"));
            listResult.setCode(200);
            listResult.setCurrPage(Integer.valueOf(page));
            listResult.setPageSize(Integer.valueOf(size));
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.PortalFeedback.pagePortalFeedback, method = RequestMethod.GET)
    @ApiOperation(value = "公众健康服务--获取意见反馈列表", notes = "公众健康服务--根据查询条件分页获取意见反馈列表")
    public Envelop pagePortalFeedback(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "-submitDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        Envelop  envelop = new Envelop();
        if(StringUtils.isEmpty(sorts)){
            sorts = "-submitDate";
        }
        List<PortalFeedback> portalFeedbackList = portalFeedbackService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalFeedbackService.getCount(filters), page, size);
        List<MPortalFeedback> list = (List<MPortalFeedback>) convertToModels(portalFeedbackList, new ArrayList<MPortalFeedback>(portalFeedbackList.size()), MPortalFeedback.class, fields);
        envelop.setDetailModelList(list);
        envelop.setSuccessFlg(true);
        envelop.setTotalCount((int)portalFeedbackService.getCount(filters));
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        return envelop;
    }

}