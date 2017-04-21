package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MPortalResources;
import com.yihu.ehr.portal.model.PortalResources;
import com.yihu.ehr.portal.service.PortalResourcesService;
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
 * 2017-03-11 add  by zhouj
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "portalResources", description = "资源接口", tags = {"云门户-资源接口"})
public class PortalResourcesEndPoint extends EnvelopRestEndPoint {

    @Autowired
    PortalResourcesService portalResourcesService;

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有资源数据", notes = "查询的数据在前端表格展示")
    public List<MPortalResources> getAllPortalResources(){
        List<PortalResources> list = portalResourcesService.getAllPortalResources();
        return (List<MPortalResources>) convertToModels(list, new ArrayList<MPortalResources>(list.size()), MPortalResources.class, null);
    }

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源列表", notes = "根据查询条件获取资源列表在前端表格展示")
    public List<MPortalResources> searchPortalResources(
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
        List<PortalResources> portalResourcesList = portalResourcesService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, portalResourcesService.getCount(filters), page, size);

        return (List<MPortalResources>) convertToModels(portalResourcesList, new ArrayList<MPortalResources>(portalResourcesList.size()), MPortalResources.class, fields);
    }

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源", notes = "创建资源信息")
    public MPortalResources createPortalResources(
            @ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
            @RequestBody String portalResourcesJsonData) throws Exception {
        PortalResources portalResources = toEntity(portalResourcesJsonData, PortalResources.class);
        portalResources.setUploadTime(new Date());
        portalResourcesService.save(portalResources);
        return convertToModel(portalResources, MPortalResources.class);
    }

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改资源", notes = "重新绑定资源信息")
    public MPortalResources updateDoctor(
            @ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
            @RequestBody String portalResourcesJsonData) throws Exception {
        PortalResources portalResources = toEntity(portalResourcesJsonData, PortalResources.class);
        portalResourcesService.save(portalResources);
        return convertToModel(portalResources, MPortalResources.class);
    }


    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取资源信息")
    public MPortalResources getPortalResources(
            @ApiParam(name = "portalResources_id", value = "", defaultValue = "")
            @PathVariable(value = "portalResources_id") Long portalResourcesId) {
        PortalResources portalResources = portalResourcesService.getPortalResources(portalResourcesId);
        MPortalResources portalResourcesModel = convertToModel(portalResources, MPortalResources.class);
        return portalResourcesModel;
    }


    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "根据id删除资源")
    public boolean deletePortalResources(
            @ApiParam(name = "portalResources_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "portalResources_id") Long portalResourcesId) throws Exception {
        portalResourcesService.deletePortalResources(portalResourcesId);
        return true;
    }

}