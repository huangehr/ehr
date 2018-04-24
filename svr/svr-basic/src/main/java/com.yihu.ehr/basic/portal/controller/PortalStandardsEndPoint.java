package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.model.PortalStandards;
import com.yihu.ehr.basic.portal.service.PortalStandardsService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MPortalStandards;
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
@Api(value = "portalStandards", description = "标准规范管理接口", tags = {"云门户-标准规范管理接口"})
public class PortalStandardsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PortalStandardsService portalStandardsService;


    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范列表", notes = "根据查询条件获取标准规范列表在前端表格展示")
    public List<MPortalStandards> searchPortalStandards(
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
        List<PortalStandards> portalStandardsList = portalStandardsService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalStandardsService.getCount(filters), page, size);

        return (List<MPortalStandards>) convertToModels(portalStandardsList, new ArrayList<MPortalStandards>(portalStandardsList.size()), MPortalStandards.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准规范", notes = "创建标准规范信息")
    public MPortalStandards createPortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestBody String portalStandardJsonData) throws Exception {
        PortalStandards portalStandards = toEntity(portalStandardJsonData, PortalStandards.class);

        portalStandardsService.save(portalStandards);
        return convertToModel(portalStandards, MPortalStandards.class);
    }

    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandards, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改标准规范", notes = "重新绑定标准规范信息")
    public MPortalStandards updatePortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestBody String portalStandardJsonData) throws Exception {
        PortalStandards portalStandards = toEntity(portalStandardJsonData, PortalStandards.class);
        portalStandardsService.save(portalStandards);
        return convertToModel(portalStandards, MPortalStandards.class);
    }


    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandardsAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准规范信息")
    public MPortalStandards getPortalStandard(
            @ApiParam(name = "portalStandard_id", value = "", defaultValue = "")
            @PathVariable(value = "portalStandard_id") Long portalStandardId) {
        PortalStandards portalStandards = portalStandardsService.getPortalStandards(portalStandardId);
        MPortalStandards mPortalStandardModel = convertToModel(portalStandards, MPortalStandards.class);
        return mPortalStandardModel;
    }


    @RequestMapping(value = ServiceApi.PortalStandards.PortalStandardsAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准规范", notes = "根据id删除标准规范")
    public boolean deletePortalNotice(
            @ApiParam(name = "portalStandard_id", value = "标准规范编号", defaultValue = "")
            @PathVariable(value = "portalStandard_id") Long portalStandardId) throws Exception {
        portalStandardsService.deletePortalStandards(portalStandardId);
        return true;
    }

}