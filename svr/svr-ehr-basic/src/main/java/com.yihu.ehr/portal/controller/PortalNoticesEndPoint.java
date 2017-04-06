package com.yihu.ehr.portal.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MPortalNotice;
import com.yihu.ehr.portal.model.PortalNotices;
import com.yihu.ehr.portal.service.PortalNoticesService;
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
 * 2017-02-20 add  by ysj
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "portalNotices", description = "通知公告接口")
public class PortalNoticesEndPoint extends EnvelopRestEndPoint {

    @Autowired
    PortalNoticesService portalNoticesService;

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public List<MPortalNotice> getPortalNoticeTop10(){
        List<PortalNotices> list = portalNoticesService.getPortalNoticeTop10();
        return (List<MPortalNotice>) convertToModels(list, new ArrayList<MPortalNotice>(list.size()), MPortalNotice.class, null);
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告列表", notes = "根据查询条件获取通知公告列表在前端表格展示")
    public List<MPortalNotice> searchPortalNotices(
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
        List<PortalNotices> portalNoticesList = portalNoticesService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalNoticesService.getCount(filters), page, size);

        return (List<MPortalNotice>) convertToModels(portalNoticesList, new ArrayList<MPortalNotice>(portalNoticesList.size()), MPortalNotice.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建通知公告", notes = "创建通知公告信息")
    public MPortalNotice createPortalNotice(
            @ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
            @RequestBody String portalNoticeJsonData) throws Exception {
        PortalNotices portalNotice = toEntity(portalNoticeJsonData, PortalNotices.class);
        portalNotice.setReleaseDate(new Date());
        portalNoticesService.save(portalNotice);
        return convertToModel(portalNotice, MPortalNotice.class);
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改通知公告", notes = "重新绑定通知公告信息")
    public MPortalNotice updatePortalNotice(
            @ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
            @RequestBody String portalNoticeJsonData) throws Exception {
        PortalNotices portalNotice = toEntity(portalNoticeJsonData, PortalNotices.class);
        portalNoticesService.save(portalNotice);
        return convertToModel(portalNotice, MPortalNotice.class);
    }


    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取通知公告信息")
    public MPortalNotice getPortalNotice(
            @ApiParam(name = "portalNotice_id", value = "", defaultValue = "")
            @PathVariable(value = "portalNotice_id") Long portalNoticeId) {
        PortalNotices portalNotice = portalNoticesService.getPortalNotice(portalNoticeId);
        MPortalNotice portalNoticeModel = convertToModel(portalNotice, MPortalNotice.class);
        return portalNoticeModel;
    }


    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除通知公告", notes = "根据id删除通知公告")
    public boolean deletePortalNotice(
            @ApiParam(name = "portalNotice_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "portalNotice_id") Long portalNoticeId) throws Exception {
        portalNoticesService.deletePortalNotice(portalNoticeId);
        return true;
    }

}