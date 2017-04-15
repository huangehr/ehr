package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MPortalSetting;
import com.yihu.ehr.portal.model.PortalSetting;
import com.yihu.ehr.portal.service.PortalSettingService;
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
@Api(value = "portalSetting", description = "门户配置接口", tags = {"云门户-门户配置接口"})
public class PortalSettingEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PortalSettingService portalSettingService;



    @RequestMapping(value = ServiceApi.PortalSetting.PortalSettingTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public List<MPortalSetting> getMPortalSettingTop10(){
        List<PortalSetting> list = portalSettingService.getPortalSettingTop10();
        return (List<MPortalSetting>) convertToModels(list, new ArrayList<MPortalSetting>(list.size()), MPortalSetting.class, null);
    }


    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.GET)
    @ApiOperation(value = "获取门户配置列表", notes = "根据查询条件获取门户配置列表在前端表格展示")
    public List<MPortalSetting> searchPortalSetting(
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
        List<PortalSetting> portalSettingList = portalSettingService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalSettingService.getCount(filters), page, size);

        return (List<MPortalSetting>) convertToModels(portalSettingList, new ArrayList<MPortalSetting>(portalSettingList.size()), MPortalSetting.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建门户配置", notes = "创建门户配置信息")
    public MPortalSetting createPortalSetting(
            @ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
            @RequestBody String portalSettingJsonData) throws Exception {
        PortalSetting portalSetting = toEntity(portalSettingJsonData, PortalSetting.class);

        portalSettingService.save(portalSetting);
        return convertToModel(portalSetting, MPortalSetting.class);
    }

    @RequestMapping(value = ServiceApi.PortalSetting.PortalSetting, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改门户配置", notes = "重新绑定通知门户配置")
    public MPortalSetting updatePortalSetting(
            @ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
            @RequestBody String portalSettingJsonData) throws Exception {
        PortalSetting portalSetting = toEntity(portalSettingJsonData, PortalSetting.class);
        portalSettingService.save(portalSetting);
        return convertToModel(portalSetting, MPortalSetting.class);
    }


    @RequestMapping(value = ServiceApi.PortalSetting.PortalSettingAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取通知门户配置")
    public MPortalSetting getPortalSetting(
            @ApiParam(name = "portalSetting_id", value = "", defaultValue = "")
            @PathVariable(value = "portalSetting_id") Long portalSettingId) {
        PortalSetting portalSetting = portalSettingService.getPortalSetting(portalSettingId);
        MPortalSetting portalSettingModel = convertToModel(portalSetting, MPortalSetting.class);
        return portalSettingModel;
    }


    @RequestMapping(value = ServiceApi.PortalSetting.PortalSettingAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除门户配置", notes = "根据id删除门户配置")
    public boolean deletePortalSetting(
            @ApiParam(name = "portalSetting_id", value = "门户配置编号", defaultValue = "")
            @PathVariable(value = "portalSetting_id") Long portalSettingId) throws Exception {
        portalSettingService.deletePortalSetting(portalSettingId);
        return true;
    }

}